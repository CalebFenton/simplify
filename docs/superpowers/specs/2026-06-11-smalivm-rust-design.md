# smalivm Rust Clean-Break Design

Date: 2026-06-11

Status: Draft for review

## Purpose

Port `smalivm` to Rust with a clean-break architecture. Do not preserve Java APIs or old class shapes unless they encode a product requirement.

Primary goal: execute Dalvik bytecode, track machine state across paths, and expose enough recorded state for future `simplify` and `sdbg` ports.

The hard requirement is semantic concreteness. Unknown values spread aggressively and destroy deobfuscation value. The old `safe_*` configs mattered because every extra concrete framework/JDK behavior unlocked cascading constants. The Rust design must preserve that lesson instead of treating framework execution as optional acceleration.

## Current smalivm Summary

Old `smalivm` is an unknown-aware Dalvik virtual executor.

- It loads APK/dex/smali through baksmali/dexlib2.
- It builds `VirtualClass`, `VirtualMethod`, and `VirtualField` wrappers around dexlib2 objects.
- `VirtualMachine` creates an `ExecutionContext`, initializes classes, and executes methods through `MethodExecutor` and `NodeExecutor`.
- `ExecutionGraph` stores node piles per code address. Template nodes hold op definitions; runtime nodes hold visited states.
- `MethodState` stores Dalvik registers plus pseudo-registers for result, return, throw, exception, and return-address.
- `Heap` uses parent-chain sparse state so child execution contexts can read prior state and write local overrides.
- When a child reads an ancestor heap value, the old heap deep-clones the value and pulls down all still-valid register aliases that pointed at the same object. This preserves path history while keeping Java object identity consistent inside one path.
- Unknown predicates fork execution paths.
- `InvokeOp` handles too much: dispatch, local calls, safe reflection, framework behavior, unknown fallback, object mutation, constructor semantics, and side effects.
- Exceptions are bolted on around opcode execution instead of being first-class successors.
- Java reflection and generated JVM classes tie framework behavior to host JVM object state.
- Old dynamic class generation was not an execution strategy for app bytecode. It generated JVM class shapes so `Class<?>`, `Field`, enum helpers, constructor lookup, and reflected signatures could exist for smali/app classes.
- Old `Class.forName` loaded generated classes for unsafe/local classes, then initialized static state in VM-owned `ClassState`; JVM shape statics were not trusted as the source of truth.
- Old `Field.get` supported safe/framework fields through Java reflection and local app static fields through `StaticFieldAccessor`. Instance app-field reflection was explicitly unsupported and returned unknown.

The Rust port should preserve the execution graph and path-state idea, but not the old class structure.

## Reference Implementation Contracts

The old code is the behavioral reference, even where its structure is bad.

| Old component | Contract to preserve | Rust/Kotlin design |
| --- | --- | --- |
| `ExecutionGraph` | Node piles by address, reached-address checks, terminating-address consensus, side-effect aggregation | `ExecutionGraph` + `NodeDelta` + `AnalysisView` |
| `ExecutionContext` | Parent-chain path state, class init state, call depth/caller tracking | persistent `State`, `CallStack`, `ClassInitSnapshot` |
| `Heap` | Deep-clone-on-read from ancestor and preserve aliases | identity-aware persistent heap and host heap versions |
| `MethodState` | Registers plus pseudo-registers for result/return/throw/exception/return-address | typed register file with pseudo-register namespace |
| `TemplateStateFactory` | Method params start unknown; instance param starts uninitialized; class fields get literal initial values | method/class state templates |
| `InvokeOp` | Dispatch local/non-local calls, safe policy, unknown fallback, mutable arg propagation | engine-owned call dispatch and `CallSummary` |
| `collapseMultiverse` | Join callee paths by consensus before returning to caller | explicit call-boundary join rules |
| `ExceptionHandlerAddressResolver` | Resolve most-specific catch first, then catch-all in active try range | first-class exceptional successors |
| `ClassBuilder`/`SmaliClassLoader` | Generate JVM class shape for app/framework classes missing on host JVM | Kotlin `DexShapeClassLoader` |
| `MethodReflector` | Execute allowlisted JVM methods only when args are known | Kotlin JVM backend policy and transactions |
| `StaticFieldAccessor` | Safe/framework statics via JVM reflection, app statics via VM state; ignore unsafe static writes | split static ownership and bridge deltas |
| `MethodEmulator` | Hand models for reflection and VM-specific behavior | `RustModelBackend`/Kotlin shims |
| `safe_*` configs | Semantic allow/deny/immutability surface | `PolicyCompiler` and policy debt reporting |

Anything not in this table must still be checked against the old implementation before planning implementation. No greenfield invention when reference behavior already exists.

## Architectural Decisions

These are design decisions. Input convenience, parser choice, and APK zip handling are implementation boundaries, not architectural centerpieces.

### AD-01: State Is Persistent Path State Plus Recorded Deltas

Do not store one giant mutable VM state and hope to reconstruct history. Every execution path owns a persistent state overlay. Every executed instruction records the delta it caused.

Reference behavior: old `ExecutionContext` forms parent chains; old `Heap.get` clones ancestor values into the child path on read; old `ExecutionGraph` stores one node per address visit and exposes consensus queries over node piles.

Rust design:

- `State` is the current frontier state for scheduling.
- `ExecutionGraph` is append-only history for analysis.
- `NodeDelta` records register, heap, static, class-init, host-JVM, exception, and side-effect changes.
- `AnalysisView` derives consensus and simplification facts from deltas/history, not by exposing mutable VM internals.

This is the core product. `simplify` exists because state was recorded at opcode granularity.

### AD-02: Object Identity Is Explicit and Path-Scoped

Old smalivm used Java object identity (`==`) and deep cloning to preserve aliases across forks. Rust cannot rely on host object identity for Dalvik objects.

Rust design:

- every Rust-owned object has `ObjectId`
- every JVM-owned object has `JvmObjectId`
- object values never embed path version
- current state determines which version of an object identity is visible
- reads/mutations preserve alias groups exactly like old `Heap.get` and `assignRegisterAndUpdateIdentities`

If two registers alias before a path mutation, they must alias after the cloned path mutation. If an alias was reassigned before the clone, it must not be rewritten. This invariant is more important than the storage data structure.

### AD-03: Execution Graph Is the Public Analysis Product

`simplify` must not consume executor internals. It consumes `AnalysisView` over recorded graph data.

The public analysis contract includes:

- reached addresses
- node piles per address
- register read/write deltas
- heap/static read/write deltas
- host JVM operation events
- side-effect summaries
- exception successors
- terminating-state consensus
- host-object concretization summaries

Reference behavior: old `ExecutionGraph.getRegisterConsensus`, `getFieldConsensus`, `getTerminatingRegisterConsensus`, `getHighestSideEffectLevel`, `wasAddressReached`, and node piles are the compatibility target conceptually, not literally.

### AD-04: Unknowns Are Contagious and Must Be Accounted For

Unknown values are not benign placeholders. They destroy downstream concrete recovery.

Rust design:

- unknown branch predicates fork paths
- unknown method args prevent JVM execution unless policy says otherwise
- unknown values inside JVM-owned object graphs taint those graphs
- every fallback to unknown records a diagnostic and policy-debt event
- consensus failure returns typed unknowns, not untyped mush

Reference behavior: old `assumeMaximumUnknown` mutates mutable args to unknown and returns unknown results for unresolvable calls. The Rust design must be at least as conservative, but more observable.

### AD-05: Calls Join Through Explicit Multiverse Collapse

Method calls are not simple function calls. A callee may fork internally and terminate through multiple states.

Reference behavior: old `VirtualMachine.collapseMultiverse` merges callee terminating states back into the caller by consensus over mutable parameters and class fields. If no consensus, unknown wins.

Rust design:

- local Dalvik calls return a `CallSummary`
- `CallSummary` contains return consensus, mutable parameter deltas, static/class deltas, side effects, exceptions, and diagnostics
- caller state is updated only through this explicit summary
- no callee internal state leaks into caller except through declared summary channels

This decision replaces `InvokeOp` god-object behavior with an engine-owned call boundary.

### AD-06: Exceptions Are First-Class Successors

Exceptions are not log messages or late node patches. Every opcode/framework call can produce normal and exceptional successors.

Reference behavior: old `NodeExecutor` patched exception children after opcode execution through `ExceptionHandlerAddressResolver`; unhandled cross-method exceptions were incomplete and fragile.

Rust design:

- instruction effects can produce normal and exceptional `TransitionDraft`s
- handler resolution is part of successor construction
- cross-method propagation is represented in `CallSummary`
- JVM server exceptions return structured exception objects and become exceptional successors

### AD-07: Kotlin JVM Backend Owns Allowlisted JVM-Shaped Object State

Do not reimplement every safe class in Rust. Do not use a Rust JVM crate as primary design. Use the actual JVM for JVM-shaped work.

Rust owns Dalvik execution and path scheduling. Kotlin server owns concrete framework/JDK object graphs for allowlisted methods/classes. The two communicate through versioned heap IDs.

This preserves the old benefit: real Java objects behave like real Java objects across reflection, private fields, lazy caches, and collections.

### AD-08: JVM Heap Version Is Path State

A JVM object handle without version is wrong. Forked execution needs multiple versions of the same logical JVM object.

Rust state stores `HostHeapRef { session_id, version_id }`. `Value::HostObject` stores only stable `JvmObjectId` and class.

Mutation flow:

```text
begin tx from current version
invoke JVM method on cloned/materialized heap
commit tx -> new version + deltas + return/exception
update Rust state's HostHeapRef
```

This is the JVM-server equivalent of old smalivm's deep-clone-on-read heap behavior.

### AD-09: App/Dex State Remains Rust-Owned Even When Reflected In JVM

Generated JVM classes are shape/mirror classes. They are not a second source of truth.

Reference behavior: old `Class.forName` loaded generated JVM classes for local classes, then initialized static state in VM-owned `ClassState`; old `Field.get` read app static fields from `StaticFieldAccessor`; app instance fields returned unknown.

Rust design:

- app class identity, app statics, app object fields, and app method execution are Rust-owned
- Kotlin shape classes provide `Class<?>`, `Field`, `Method`, constructor, enum, annotation, and signature shape
- app statics are materialized into Kotlin only for a transaction and extracted back as deltas
- app instance object materialization requires explicit adapters; absent adapter means unknown/fallback

### AD-10: Class Loader Identity Is Path State

Descriptor-only class identity is wrong once runtime dex loading exists.

Rust design:

- `ClassId = (LoaderId, descriptor)`
- root APK dex units live in the initial app loader snapshot
- runtime dex attach creates/updates dynamic loaders in the current path state
- Kotlin shape loaders mirror Rust loader identity for reflection
- duplicate descriptors across loaders are legal and distinct

This decision matters more than “APK support.” APK support is just one source of dex units.

Runtime class loading is not global. If an unknown branch attaches a dex on one path, sibling paths must not see it. `ClassLoaderGraph` describes loader identities and parent relationships; `LoaderSnapshot` in `State` describes which dex units/unknown sources are visible on that path.

### AD-11: Policy Is Compiled From Reference Configs and Produces Debt

Old `safe_*` configs encode hard-earned semantic surface. They are not junk files.

Rust design:

- import old configs through `PolicyCompiler`
- convert broad class allows into typed method/effect rules
- unsafe denies override broad allows
- immutable classes are optimization hints, not proof of no private mutation
- every missed semantic opportunity is logged as policy debt with frequency/context

This gives us a growth loop: corpus execution ranks the next methods/classes to support.

### AD-12: Framework Execution Is Allowlisted and Disposable

Kotlin server uses real JVM execution, but it is not a general sandbox for arbitrary Java behavior.

Rust/Kotlin policy must deny or model filesystem, network, thread, time, random, native, process-exit, binder, Android service, and resource behavior unless explicitly allowed.

The server process is disposable. Timeout or crash becomes a recorded fallback event.

### AD-13: Parser and Input Format Are Replaceable Front Ends

DEX parser choice, APK zip extraction, and raw dex input are implementation boundaries around `DexStore`. They must not leak into executor design.

Day-one boundaries:

- raw `.dex`
- APK root `classes*.dex` as zip entries
- runtime dex attach as new `DexUnit`
- no smali directory parser
- no manifest/resource parser

These are simple constraints, not the architecture.

## Non-Goals

- No smali parser in v1. Users can compile smali to dex.
- No Android manifest/resource parsing in v1.
- No generic symbolic solver in v1.
- No Ristretto dependency in the primary design.
- No host-owned opaque object handles without versioning.
- No unbounded execution of arbitrary framework/system APIs.
- No compatibility layer for old Java `smalivm` classes unless required by migration tooling.

## Top-Level Architecture

```text
smalivm-rs
  input
    ApkZipLoader
    DexBackend
    DexStore
  types
    TypeStore
    ClassLoaderGraph
    BootClasspath
  vm
    ExecutionEngine
    Scheduler
    ExceptionEngine
    ExecutionGraph
  state
    State
    Registers
    Heap
    StaticState
    LoaderSnapshot
    HostHeapRef
  opcode
    DalvikInstructionSemantics
  framework
    FrameworkBackend trait
    JvmServerBackend
    RustModelBackend
    StubBackend
  analysis
    AnalysisView
    GraphQueries

kotlin-jvm-backend
  RpcServer
  SessionStore
  VersionedHeap
  DexShapeClassLoader
  ShapeClassRegistry
  ReflectionInvoker
  DeepCloner
  PolicyRuntime
  AndroidProfile
```

## Rust ID and Ownership Policy

All graph/type/state identities are typed IDs, not raw `usize` aliases.

```rust
struct DexUnitId(u32);
struct LoaderId(u32);
struct TypeDescriptorId(u32);
struct ClassDefId(u32);
struct MethodId(u32);
struct FieldId(u32);
struct ObjectId(u64);
struct NodeId(u64);
```

Rules:

- descriptors, method protos, and field signatures are interned
- arena IDs are append-only for the lifetime of a `CodeVersionId`/analysis run, or use generation counters if reuse is allowed
- store IDs outside arena values; do not duplicate `id` inside every arena object unless required for serialization
- unresolved references and resolved IDs are separate types
- path-scoped provenance such as memory-backed dynamic dex source stays in `LoaderSnapshot`/events, not global `DexStore`

```rust
struct ClassRef { loader: LoaderId, descriptor: TypeDescriptorId }
struct ResolvedClassId(ClassDefId);
```

This prevents loader/path/version bugs from hiding behind string signatures.

## Input and Dex Storage

### `DexStore`

`DexStore` owns every dex unit known to the analysis.

```rust
struct DexStore {
    units: Arena<DexUnit>,
}

struct DexUnit {
    origin: DexOrigin,
    classes: Vec<ClassDefId>,
    bytes: Arc<[u8]>,
}

enum DexOrigin {
    RawInput { path: PathBuf },
    ApkRoot { apk: PathBuf, entry: String },
    ApkNested { apk: PathBuf, entry: String },
    RuntimeFile { path: PathBuf },
    RuntimeMemory { content_hash: Hash256 },
    Framework { profile: AndroidApiProfile },
    UnknownRuntime,
}
```

APK loading is intentionally simple:

- Treat APK as zip.
- Extract root entries matching `classes.dex`, `classes2.dex`, `classes3.dex`, etc.
- Sort by numeric suffix.
- Load each root dex into the app class loader.
- Ignore resources, manifest, signatures, native libraries.

Nested dex files must be representable day one, but discovery policy can be conservative. Runtime loader semantics attach nested or filesystem dex units when a modeled class loader receives concrete bytes or paths.

### `DexBackend`

Parser choice is isolated.

```rust
trait DexBackend {
    fn parse_dex(&self, bytes: &[u8], origin: DexOrigin) -> Result<DexUnitIr>;
}
```

Required metadata:

- class, method, field, proto, string, and type refs
- method code registers, ins, outs
- instruction offsets and operands
- invoke, field, method, type refs
- switch payloads
- fill-array-data payloads
- try ranges, typed handlers, catch-all handlers

LIEF can be evaluated, but architecture does not depend on it.

## Class Loading and Type Identity

Global descriptor-only identity is wrong. Dynamic dex loaders can define the same descriptor in different loaders.

```rust
struct ClassLoaderGraph {
    loaders: Arena<ClassLoaderNode>,
}

struct ClassLoaderNode {
    id: LoaderId,
    parent: Option<LoaderId>,
    lookup_policy: LookupPolicy,
}

struct LoaderSnapshot {
    visible_dex_units: BTreeMap<LoaderId, Vec<DexUnitId>>,
    unknown_runtime_sources: BTreeSet<LoaderId>,
    object_to_loader: BTreeMap<ObjectId, LoaderId>,
}

struct ClassId {
    loader: LoaderId,
    descriptor: TypeDescriptor,
}
```

Default loaders:

- boot loader: framework/JDK profile metadata
- app loader: root APK dex units
- dynamic loaders: created by modeled `DexClassLoader`, `PathClassLoader`, `InMemoryDexClassLoader`, `DexFile.loadDex`, or related APIs

Lookup is parent-first unless Android behavior requires otherwise for a specific loader.

Runtime dex attach:

- If loader args are concrete, parse and attach a new `DexUnit` to that loader in the current `LoaderSnapshot`.
- If args are unknown, attach `UnknownRuntime` marker to that loader in the current `LoaderSnapshot` and make future resolution conservative for that path.
- Duplicate class descriptors are allowed across loader IDs.

## Execution Engine

The engine schedules states and records graph nodes. Opcode handlers are boring and local.

```rust
trait DalvikInstructionSemantics {
    fn step(&self, state: StateView<'_>, cx: &StepCx) -> InstructionEffect;
}

enum InstructionEffect {
    Transitions(Vec<TransitionDraft>),
    Request(EngineRequest),
    Abort(AbortReason),
}

enum EngineRequest {
    Invoke(InvokeRequest),
    ClassInit(ClassInitRequest),
    DynamicLoad(DynamicLoadRequest),
}

struct TransitionDraft {
    kind: TransitionKind,
    guard: Option<PathGuard>,
    delta: DeltaDraft,
}

enum TransitionKind {
    Normal { next_pc: CodeAddress },
    Exceptional(ExceptionSuccessor),
    Return(Option<Value>),
    Throw(Value),
}
```

Opcode handlers are not allowed to mutate `State` directly. They read through `StateView` and return draft effects. The engine owns validation, delta commit, graph recording, call dispatch, class init, dynamic loads, and exception successor construction.

Important rule: invoke handling is engine/framework work, not `InvokeOp` god-object work. `invoke-*` opcodes gather operands and emit `EngineRequest::Invoke`; they do not call methods themselves.

### State

```rust
struct State {
    path_id: PathId,
    method: MethodId,
    pc: CodeAddress,
    registers: Registers,
    heap: HeapSnapshot,
    statics: StaticSnapshot,
    class_init: ClassInitSnapshot,
    loaders: LoaderSnapshot,
    call_stack: CallStack,
    host_heap: Option<HostHeapRef>,
    path_guards: Vec<PathGuard>,
    limits: ExecutionLimits,
}

struct HostHeapRef {
    session_id: SessionId,
    version_id: JvmVersionId,
}
```

Dalvik heap values are Rust-owned.

```rust
enum Value {
    Unknown { ty: TypeDescriptor, reason: UnknownReason, source: UnknownSource },
    Null(TypeDescriptor),
    Bool(bool),
    I32(i32),
    I64(i64),
    F32(f32),
    F64(f64),
    String(String),
    Object(ObjectId),
    Array(ObjectId),
    ClassMirror(ClassId),
    MethodMirror(MethodId),
    FieldMirror(FieldId),
    HostObject { object_id: JvmObjectId, class: ClassId },
}
```

`HostObject` identity is stable across paths. The current concrete host object state is determined by `state.host_heap.version_id`.

On Rust path fork:

```text
child.host_heap = parent.host_heap
```

On JVM-backed mutation:

```text
state.host_heap.version_id = new_version_returned_by_jvm_server
```

### Heap Forking Invariant

Old `Heap.get` is the reference behavior for Rust path state:

- child states are sparse overlays over parent states
- reading an ancestor object pulls a clone into the child path
- all still-valid aliases to the same object are pulled down together
- reassigned aliases are not pulled down
- uninitialized-instance replacement updates every alias that still points to that identity

Rust should implement this as persistent path state with explicit object IDs, but it must preserve the same aliasing invariant. If two registers point at one object before a path mutation, they must point at the same cloned object after the path reads/mutates it. If one register was reassigned before the read, it must not be rewritten.

### Recorded Deltas

Every executed node records what changed. This is the data `simplify` cares about.

```rust
struct NodeDelta {
    node_id: NodeId,
    path_id: PathId,
    method: MethodId,
    address: CodeAddress,
    reads: ReadSet,
    writes: WriteSet,
    successors: Vec<SuccessorId>,
    exceptions: Vec<ExceptionSuccessor>,
    side_effect: SideEffectLevel,
    ordered_accesses: Vec<AccessEvent>,
    host_events: Vec<HostEvent>,
    diagnostics: Vec<Diagnostic>,
}

enum AccessEvent {
    RegisterRead(RegisterRead),
    RegisterWrite(RegisterWrite),
    HeapRead(HeapRead),
    HeapWrite(HeapWrite),
    StaticRead(StaticRead),
    StaticWrite(StaticWrite),
    ClassInitRead(ClassInitRead),
    ClassInitWrite(ClassInitWrite),
    HostRead(HostRead),
    HostWrite(HostVersionWrite),
}

struct ReadSet {
    registers: Vec<RegisterRead>,
    heap_fields: Vec<HeapRead>,
    static_fields: Vec<StaticRead>,
    class_init: Vec<ClassInitRead>,
    host_fields: Vec<HostRead>,
}

struct WriteSet {
    registers: Vec<RegisterWrite>,
    heap_fields: Vec<HeapWrite>,
    static_fields: Vec<StaticWrite>,
    class_init: Vec<ClassInitWrite>,
    host_version: Option<HostVersionWrite>,
}

struct HostVersionWrite {
    before: HostHeapRef,
    after: HostHeapRef,
    summary: HostCommitSummary,
}
```

The graph may keep compact summaries, not full state copies. Frontier `State` is for execution. `NodeDelta` and summaries are for analysis.

`ordered_accesses` is required because simplification and liveness care whether a register was read before it was reassigned inside one instruction. The aggregate read/write sets are indexes; they are not the source of truth.

### State Mutation Boundary

All execution-time mutation goes through an engine-owned delta builder.

```rust
struct StateView<'a> { /* read-only state view */ }
struct DeltaBuilder { /* records writes and access order */ }
```

Opcode handlers, Rust models, and framework backends may request effects, but the engine commits them. This prevents silent mutations that are not recorded in `ExecutionGraph`.

Bad pattern:

```rust
fn step(&self, state: &mut State) { state.registers.write(...); }
```

Required pattern:

```rust
fn step(&self, state: StateView<'_>) -> InstructionEffect;
// engine validates effect, commits through DeltaBuilder, records NodeDelta
```

This is how the Rust port avoids the old tendency to expose every internal object to `simplify` while still giving `simplify` the same power: reads, writes, consensus, side effects, and concrete values are first-class facts.

## Exceptions

Exceptions are first-class state transitions.

```rust
struct ExceptionSuccessor {
    throwable: ThrowableValue,
    handler_pc: Option<CodeAddress>,
    propagation: ExceptionPropagation,
}
```

Throwing opcodes, implicit null checks, array bounds, class init failures, framework calls, and JVM server calls all return exceptional successors.

Cross-method propagation is engine-owned. Handlers are resolved through method exception metadata and type hierarchy.

## Framework and JDK Semantics

The framework layer chooses how a method executes.

```rust
trait FrameworkBackend {
    fn alloc(&mut self, request: AllocRequest, view: StateView<'_>) -> Result<BackendEffect>;
    fn invoke(&mut self, request: CallRequest, view: StateView<'_>) -> Result<BackendEffect>;
    fn get_field(&mut self, request: FieldGetRequest, view: StateView<'_>) -> Result<BackendEffect>;
    fn set_field(&mut self, request: FieldSetRequest, view: StateView<'_>) -> Result<BackendEffect>;
}
```

Backends return effects; they do not mutate `State`. The engine commits `BackendEffect` through the same delta path as opcode effects. This keeps host/JVM effects visible to `ExecutionGraph`.

Backends:

- `JvmServerBackend`: Kotlin JVM server with versioned heap. Primary for allowlisted Java/JDK/Android framework classes.
- `RustModelBackend`: Rust-native models for Dalvik-specific mirrors, class loaders, cheap primitives, and policies that should not leave Rust.
- `StubBackend`: conservative unknown/effect fallback.

The policy layer decides backend per method. Backend choice is not automatic reflection.

## Call Boundary and Joins

The old call join is `VirtualMachine.collapseMultiverse`. It is expensive and blunt, but conceptually right: the caller receives only facts that survive all terminating callee paths, plus side-effect metadata.

Rust makes this explicit.

```rust
struct CallSummary {
    callee: MethodId,
    terminating_paths: Vec<PathId>,
    return_value: Consensus<Value>,
    mutable_parameter_writes: Vec<ParameterWriteConsensus>,
    static_writes: Vec<StaticWriteConsensus>,
    host_commit: Option<HostCommitConsensus>,
    exceptions: Vec<ExceptionSummary>,
    side_effect: SideEffectLevel,
    diagnostics: Vec<Diagnostic>,
}

enum Consensus<T> {
    Known(T),
    Unknown { ty: TypeDescriptor, reason: ConsensusFailure },
    Unreachable,
}
```

Join rules:

- if all non-exception terminating paths agree on a return value, caller gets that value
- if values differ, caller gets typed unknown
- mutable parameters join by identity-aware consensus, matching old `getMutableParameterConsensus`
- static fields join by field consensus; uninitialized in any relevant path means unknown
- host JVM versions join only if all paths produce the same host version or a mergeable host summary; otherwise affected host objects become tainted/unknown in caller
- if every terminating path throws, normal children are removed and only exceptional successors remain
- if some paths throw and some return, both normal and exceptional successors are recorded

This removes hidden call effects from opcode code and makes simplification facts inspectable.

### Dalvik Invoke ABI

Invoke behavior is a compatibility contract, not just opcode mechanics.

The engine must implement:

- invoke kind dispatch: static, direct, super, virtual, interface, and range forms
- argument register walking, including wide `J`/`D` values
- receiver null checks before target dispatch
- virtual/interface target resolution from concrete runtime type when known
- inherited method lookup through class hierarchy
- primitive/reference coercions matching old behavior
- `0:I` as null only when passed to a reference-typed parameter
- subtype retention: fallback unknowns use the most specific analyzed runtime type, not only declared `Object`
- array/Object `clone` special behavior
- constructor lifecycle: uninitialized instance identity, `<init>` completion, superclass constructor effects, and alias replacement
- local call depth/visit/time limit fallback

Mutable call arguments are represented by `ParameterRoot`s independent of terminal callee registers. If a callee overwrites `p0` but mutates the original object through another alias, caller mutation propagation still uses the original parameter root.

```rust
struct InvokeRequest {
    kind: InvokeKind,
    target_ref: MethodRef,
    argument_registers: Vec<Register>,
    analyzed_args: Vec<AnalyzedArgument>,
}

struct ParameterRoot {
    parameter_index: usize,
    caller_register: Register,
    before: ValueSummary,
    mutable_identity: Option<IdentityRoot>,
}
```

This replaces old `InvokeOp.analyzeParameterTypes`, `executeLocalMethod`, `assumeMaximumUnknown`, and `updateCallerForReturn` with explicit engine behavior.

### Class Initialization Semantics

App/dex class initialization is Rust-owned path state.

Triggers:

- static field get/put
- static method invoke
- `new-instance` before constructor execution where Dalvik semantics require initialized class
- `Class.forName` when initialize mode is true
- explicit class-init requests from models/framework bridge

Rules:

- initialize ancestors before class where required
- initialize literal/static field defaults before running `<clinit>`
- record `ClassInitSnapshot` writes and side-effect level in `NodeDelta`
- class init success/failure is path-local
- if class may or may not initialize across paths, static field consensus becomes unknown
- failed `<clinit>` becomes an exceptional successor and records partial side effects conservatively

Framework/JDK class initialization in Kotlin follows separate eligibility rules under the JVM backend. App shape-class initialization is never authoritative.

### Consensus Semantics

Consensus never compares bare mutable IDs alone.

```rust
enum Consensus<T> {
    Known(T),
    Unknown { ty: TypeDescriptor, reason: ConsensusFailure },
    Unreachable,
}

struct ValueSummary {
    value: Value,
    mutable_state: Option<MutableStateSummary>,
}
```

Rules:

- primitives/strings/classes compare by semantic value and type
- null is typed and joins with reference types according to type hierarchy
- mutable Rust objects compare by object identity plus path-local object state summary
- mutable JVM objects compare by `JvmObjectId` plus `HostHeapRef`/host summary
- divergent mutable object contents become typed unknown or tainted summary, not `Known(ObjectId)`
- arrays are mutable objects, not by-value wire blobs, unless a method-specific adapter proves by-value behavior
- type consensus uses most-specific common ancestor where old `ExecutionGraph` did; otherwise typed unknown

Host-version joins use a JVM merge operation:

```text
merge_versions(base, [v11, v12, ...]) -> merged_version + per-object taint/unknown summaries
```

If the server cannot merge an object graph safely, every affected alias becomes typed unknown/tainted in the merged version. Unaffected objects keep their concrete versioned state.

### Side-Effect Lattice

Preserve the old side-effect levels conceptually:

```rust
enum SideEffectLevel {
    None,
    Weak,
    Strong,
}
```

Rules:

- no state mutation and deterministic pure value recovery: `None`
- VM-local mutation, object/static mutation, or modeled safe mutation: `Weak`
- unknown external effects, unsafe calls, IO, native/system behavior, or denied side effects: `Strong`
- graph/method side-effect summary is max over reached nodes/callees

`simplify` must be able to query side effects at instruction, method, and call-summary granularity.

### Execution Limits and Fallback

Execution limits are scheduler/run state, not cloned blindly into each path.

Limits include:

- max address visits per method
- max method visits
- max call depth
- max wall/instruction budget
- max JVM transaction time
- max host heap versions/objects

When a local callee exceeds limits, caller receives the conservative unknown/mutable-argument fallback used by `assumeMaximumUnknown`: return value unknown by declared/analyzed return type, mutable arguments unknown by analyzed runtime type, and side effect at least `Weak` or `Strong` depending on failure reason.

Root execution limit failure aborts the graph with diagnostics instead of pretending method completed.

### Intentional Reference Breaks

Some old behavior is incomplete or unsafe. The spec must name breaks explicitly.

| Area | Old behavior | Rust design |
| --- | --- | --- |
| app instance fields | many `iget` paths returned typed unknown; `iput` mostly side-effected register | Rust should eventually model app instance fields in Rust heap; until implemented, tests expecting old unknown are marked intentional fallback |
| safe framework static writes | old `StaticFieldAccessor.putField` ignored safe static writes | Kotlin backend may version framework statics only for classes eligible for reset/materialization; otherwise deny/fallback |
| immutable classes | old immutability prevented caller-visible mutation; private lazy caches still existed | split caller-visible semantic immutability from private JVM-versioned mutation |
| commented tests | many high-signal tests are disabled | behavior still audited; not ignored because active flag is unreliable archaeology |

## Kotlin JVM Backend

### Purpose

The Kotlin JVM backend exists to preserve real JVM/framework object semantics while still supporting Rust path forking.

It is not an acceleration cache. It is the semantic owner for allowlisted JVM-shaped objects.

It handles cases that are painful to reimplement correctly:

- private fields
- lazy caches like `String.hash`
- reflection reads/writes
- collection internals
- `JSONObject` and similar mutable framework objects
- Android/JDK utility code present in real jars

### Ownership Model

Rust owns execution paths. Kotlin owns JVM object state.

IDs:

```text
SessionId       one analysis run
PathId          Rust path identity
JvmObjectId     stable logical JVM object identity
JvmVersionId    immutable snapshot/version of JVM heap state
TxId            mutable transaction based on one version
```

Rust state stores one current `JvmVersionId`. `HostObject` values store only `JvmObjectId` and class.

This avoids putting version IDs into every value and matches path semantics.

### Version Semantics

Example:

```text
v10 contains JSONObject object #7 = {}

Path A and Path B both reference object #7 and host version v10.

Path A invokes #7.put("a", 1):
  begin_tx(v10)
  invoke(#7, put, ["a", 1])
  commit -> v11
  Path A host version = v11

Path B still has host version v10.
```

The logical object ID is stable. Object state changes by version.

### Host Snapshot Clone Invariant

The JVM backend must treat a version as an object graph, not as a bag of independent receiver objects.

Example:

```text
v10:
  A#1.b -> B#2
  register r0 = HostObject(A#1)
  register r1 = HostObject(B#2)
```

If path A mutates `A#1`, the server must not clone only `A#1` and leave `B#2` pointing at the old live object. That would break identity: `A#1.b` in the new path would not be the same object as `r1`.

Required behavior:

```text
begin_tx(v10)
  clone/materialize the whole host snapshot, or at minimum the full alias closure
  preserve JvmObjectId mapping: A#1 -> A'#1, B#2 -> B'#2
  rewire cloned fields so A'#1.b -> B'#2
  invoke method
commit -> v11
```

After commit:

- path with `v11` sees `A#1.b` and `HostObject(B#2)` as the same cloned object
- paths still on `v10` see the old objects
- if the method mutated `B#2` through `A#1.b`, that mutation is visible through `HostObject(B#2)` in `v11`
- if `B#2` had already been mutated in the base version, the clone starts from that base state

V1 should clone the entire host snapshot for a transaction. That is blunt and expensive, but it is the correctness baseline. Later optimizations may clone only reachable components or use copy-on-write, but only if they preserve the same alias-closure invariant and pass explicit alias tests.

This is separate from graph storage. The server may deep-clone full state to execute correctly while Rust records only deltas and summaries in `ExecutionGraph`.

### Server Storage

Initial implementation can be intentionally dumb and correct.

```kotlin
data class Session(
    val id: SessionId,
    val classLoader: URLClassLoader,
    val versions: MutableMap<JvmVersionId, HeapSnapshot>,
)

data class HeapSnapshot(
    val objects: MutableMap<JvmObjectId, Any?>,
    val statics: StaticSnapshot,
    val classInitState: ClassInitSnapshot,
)
```

Write transactions clone the base snapshot, invoke methods on the clone, then commit a new immutable version.

The clone must preserve logical object IDs:

- original object `#7` maps to cloned Java object for `#7` in the new snapshot
- object graph cycles are preserved
- shared object identity inside the snapshot is preserved
- newly allocated or newly returned JVM objects receive fresh `JvmObjectId`s during commit
- objects reachable only through mutated fields/statics are discovered and registered during commit
- external aliases to nested objects remain coherent because every cloned reference is rewired through the same `JvmObjectId` map

Use reflection graph cloning first. Serialization cloning is too limited. Kryo/Objenesis can be evaluated after correctness works.

### Static State

Static fields have split ownership.

- App/dex static fields are Rust-owned in `StaticSnapshot`, matching old `ClassState` and `StaticFieldAccessor` behavior.
- Framework/JDK static fields for JVM-server classes are Kotlin-owned in `HeapSnapshot.statics`.
- Shape-class static fields are temporary materialization of Rust-owned app statics, not authoritative state.

Framework/JDK static fields are part of JVM version state.

This is mandatory because framework methods can mutate static fields and reflection can read/write them.

JVM static fields are process-global, so the server cannot pretend snapshots happen automatically. Before invoking a transaction, the server must materialize the selected version's static field values into the live JVM classes. After invocation, it must extract changed static fields back into the committed `StaticSnapshot`.

This requires single-threaded per-session execution at first. Concurrent transactions against the same class loader would corrupt static state.

Server operations that trigger framework/JDK class initialization must record class init side effects in the transaction snapshot. Rust path forking then gets separate static worlds. If class initialization cannot be restored safely for a framework class, policy must deny or stub that class rather than silently sharing global state.

JVM class initialization is one-way per `Class<?>`/classloader. Therefore framework classes are classified before execution:

- `Resettable`: statics and init side effects can be materialized/extracted safely in one session loader.
- `IsolatedLoader`: execution requires a disposable loader/process per transaction/version.
- `ModelOnly`: use Rust/Kotlin model; do not execute bytecode.
- `Denied`: fallback/unknown.

The backend must not claim versioned framework statics for classes outside `Resettable` or `IsolatedLoader`.

App/dex class initialization remains Rust-owned. This follows the old `java_lang_Class_forName` behavior: load a generated JVM class so reflection gets a `Class<?>`, then run/record app `<clinit>` in VM state and use VM-owned static values.

The Kotlin server must never treat app shape-class statics as authoritative. Before any transaction that can inspect app statics, Rust sends the relevant static field values. After the transaction, Kotlin returns only allowed static writes as deltas for Rust to commit to the current path.

### Transport

Use local RPC over Unix domain socket by default.

Protocol v1 uses length-prefixed binary frames. JSON is only for debug traces, never the hot-path wire format.

Encoding requirements:

- compact binary messages, preferably protobuf or FlatBuffers
- interned IDs for repeated class/method/field/type signatures
- byte blobs for primitive arrays and strings
- batchable operations so one frame can include transaction begin, invocation, reads, and commit
- deterministic trace logging that can render frames as JSON for debugging

Every request has:

```text
request_id
session_id
operation
payload
timeout_ms
```

Every response has:

```text
request_id
status
payload | error
diagnostics
```

The server should be single-threaded per session at first. Determinism beats throughput.

### Verbs

Session lifecycle:

```text
ping()
create_session(profile, classpath, policy) -> SessionId, initial_version
drop_session(session)
```

Version lifecycle:

```text
begin_tx(session, base_version) -> TxId
commit_tx(session, tx) -> JvmVersionId
abort_tx(session, tx)
clone_version(session, base_version) -> JvmVersionId
merge_versions(session, base_version, versions, merge_policy) -> MergeResult
drop_version(session, version)
```

Object and method operations:

```text
alloc(tx, class, ctor_sig, args) -> WireValue
invoke(tx, receiver?, method_sig, args) -> InvokeResult
get_field(tx_or_version, object, field_sig) -> WireValue
set_field(tx, object, field_sig, value) -> Unit
get_static(tx_or_version, class, field_sig) -> WireValue
set_static(tx, class, field_sig, value) -> Unit
describe_object(version, object) -> ObjectDescription
```

`alloc` and `invoke` run inside a transaction. Returned object IDs are provisional until commit. If the transaction aborts, provisional IDs are invalid and must not enter Rust state.

```rust
struct InvokeResult {
    return_value: Option<WireValue>,
    exception: Option<JvmException>,
    provisional_objects: Vec<JvmObjectId>,
    diagnostics: Vec<JvmDiagnostic>,
}

struct CommitResult {
    version_id: JvmVersionId,
    return_value: Option<WireValue>,
    exception: Option<JvmException>,
    allocated_objects: Vec<CommittedObject>,
    app_static_deltas: Vec<AppStaticDelta>,
    app_instance_deltas: Vec<AppInstanceDelta>,
    diagnostics: Vec<JvmDiagnostic>,
}
```

Simple calls may use an atomic helper:

```text
invoke_commit(session, base_version, receiver?, method_sig, args) -> CommitResult
```

The atomic helper is sugar for `begin_tx`, `invoke`, and either `commit_tx` or `abort_tx`. Guest Java exceptions commit: mutations before the throw are semantic side effects and return an exceptional `CommitResult`. Backend failures, policy denial before semantic execution, protocol errors, and clone failures abort.

`merge_versions` is used when a callee or branch rejoins multiple JVM versions. It creates a real version. Per object/field, state is concrete only if versions agree; otherwise affected objects/fields become tainted or typed unknown according to merge policy. Picking an arbitrary child version is forbidden.

Class/mirror operations:

```text
resolve_class(name, loader_policy) -> WireValue::Class
get_declared_method(class, name, params) -> WireValue::Method
get_declared_field(class, name) -> WireValue::Field
```

Server-to-Rust callbacks use the same framed protocol with reversed request direction. Required callbacks:

```text
resolve_shape_from_rust(session, loader_id, binary_name, init_mode) -> ClassShape | NotFound
read_app_static(session, tx, class, field) -> WireValue
write_app_static(session, tx, class, field, value) -> Unit
invoke_app_reflective(session, tx, method_ref, receiver, args) -> RustCallResult
```

The transport must support reentrant requests or a separate callback channel. Lazy shape resolution without a callback protocol is invalid.

Debug/GC:

```text
list_versions(session)
version_stats(session, version)
retain_versions(session, live_versions)
```

### Wire Values

```rust
enum WireValue {
    Null,
    Bool(bool),
    I32(i32),
    I64(i64),
    F32(f32),
    F64(f64),
    Char(u16),
    String(String),
    Bytes(Vec<u8>),
    ArrayObject { object_id: JvmObjectId, class: WireClassRef },
    Object { object_id: JvmObjectId, class: WireClassRef },
    Class(WireClassRef),
    Method(WireMethodRef),
    Field(WireFieldRef),
    Unknown { type_desc: String },
}
```

Arrays are mutable objects when they cross the JVM boundary. Primitive-array bytes may be embedded in summaries or pure-call arguments, but any array that can be observed or mutated by identity travels as `ArrayObject`.

Member refs must preserve loader identity and accessibility state.

```rust
struct WireMethodRef {
    declaring_class: WireClassRef,
    name: String,
    proto: String,
    accessible: bool,
    owner: MemberOwner,
}

struct WireFieldRef {
    declaring_class: WireClassRef,
    name: String,
    ty: String,
    accessible: bool,
    owner: MemberOwner,
}

enum MemberOwner {
    RustApp,
    JvmFramework,
    JvmShape,
}
```

Unknowns usually cannot cross into JVM execution. If any argument is unknown, Rust should avoid the server unless method policy says it can tolerate unknowns. The normal fallback is unknown return plus declared mutation/effect policy.

Unknowns can also contaminate host objects. If a host object graph contains an unknown value, a Rust-owned app object without an adapter, or a prior fallback mutation, the server marks the object graph tainted in that `JvmVersionId`.

Tainted host graphs follow conservative rules:

- methods that may inspect tainted fields do not execute in the JVM server
- allowlisted methods that provably ignore tainted fields may still execute
- reads of tainted fields return `Unknown`
- mutations involving tainted objects produce a new tainted version
- diagnostics record the first taint source so policy gaps become visible

This prevents fake precision where a real JVM object appears concrete but contains analysis values the JVM cannot faithfully represent.

### Exceptions

Server exceptions return structured data.

```rust
struct JvmException {
    object_id: Option<JvmObjectId>,
    version_id: JvmVersionId,
    class: String,
    message: Option<String>,
    stack: Vec<JvmStackFrame>,
}
```

Rust maps this to an exceptional successor. If the exception object is host-owned, Rust stores it as `Value::HostObject` with the returned version.

### Policy and Safety

The server only executes methods allowed by policy.

Policy decides:

- class allowed
- method allowed
- constructor allowed
- field read/write allowed
- static mutation allowed
- filesystem/network/thread/native access allowed or denied
- deterministic replacements for time/random/system properties
- fallback behavior on unknown args

The server must fail closed for unlisted dangerous behavior.

### Security and Determinism

The Kotlin server runs locally and should be killable.

Controls:

- request timeout
- max live versions
- max heap objects
- max clone depth/object bytes
- denied packages/methods
- no network by default
- no filesystem by default except configured classpath
- deterministic time/random/device properties through policy

If the server times out or crashes, Rust converts the call to configured fallback and records a diagnostic event.

Do not rely on JVM `SecurityManager`; it is obsolete and not a real sandbox. The server is controlled by allowlist, classpath construction, shims, and process isolation.

V1 execution rules:

- only allowlisted methods/classes execute
- worker process is disposable and can be killed on timeout
- OS-level sandboxing is preferred when available
- dangerous APIs are denied or replaced by profile shims before execution
- native methods not explicitly modeled are denied
- thread creation is denied unless a method-specific model owns it

This is stricter than old `safe_classes.cfg`, but it preserves the important property: safe methods improve concreteness while unsafe behavior degrades to known fallback instead of leaking side effects.

## Dynamic JVM Shape Classes

Old `smalivm` dynamically generated JVM classes from smali with ASM through `ClassBuilder` and `SmaliClassLoader`.

That mechanism was useful for a real reason:

- Java reflection APIs require real `Class<?>`, `Method`, `Field`, and constructor objects.
- Framework/JDK utility code may inspect classes, fields, annotations, interfaces, enum values, or constructor signatures.
- Safe reflected methods need JVM-visible parameter and return types.
- App/framework classes that do not exist on the host JVM still need to participate in `Class.forName`, `getDeclaredField`, `getDeclaredMethod`, `isAssignableFrom`, `instanceof`, arrays of `Class`, and enum helpers.
- Private fields and class metadata must be inspectable because malware and obfuscated code can use reflection specifically to detect lies.

The old implementation generated classes with real fields and mostly stubbed methods. Constructors were fake, `<clinit>` was empty, most methods threw `RuntimeException("Stub!")`, and enums got enough scaffolding for `values` and `valueOf`. This was crude, but the purpose was correct: give Java reflection a class-shaped thing.

The Rust/Kotlin design keeps that capability as `DexShapeClassLoader`.

### Shape Class Rules

Shape classes are generated in the Kotlin JVM server from Rust `TypeStore` metadata.

They are not app execution bodies.

For each dex/app class that must be visible to the JVM server, generate a JVM class with:

- binary/internal name matching the dex descriptor
- superclass and interface references matching Rust class metadata
- access flags where JVM verification allows them
- fields matching dex fields, including private fields where legal
- method and constructor signatures matching dex methods
- selected annotations and inner/enclosing class metadata when available
- enum scaffolding when needed
- method bodies that do not execute app bytecode by default

Default method bodies:

- `<clinit>`: no-op unless policy explicitly materializes app static state
- `<init>`: no-op or minimal verifier-satisfying body only; normal shape instance materialization should avoid constructor execution with Objenesis/Unsafe
- ordinary methods: throw a structured `UnsupportedAppMethod` exception or call an explicit bridge if policy allows
- `hashCode`: stable identity behavior, not app bytecode
- enum helpers: generated enough for reflection and common enum APIs

This preserves reflection shape without accidentally running app code in the JVM server.

Dex-valid but JVM-invalid shapes need deterministic rewrite rules, copied from reference behavior where applicable:

- interface fields that would be illegal/private in JVM classfiles are rewritten to verifier-legal flags
- overloaded fields are preserved when JVM permits lookup behavior needed by tests
- circular and self references define lazily without infinite recursion
- missing references are allowed until JVM verification/use requires resolution

Enum shape support must include the old semantic scaffolding: enum constants, `$VALUES`, `values`, `valueOf`, and any synthetic shadow instance needed for old enum reflection behavior. App enum business logic still does not execute in Kotlin unless explicitly bridged.

### Loader Identity

Each Rust `LoaderId` that needs JVM visibility maps to one Kotlin `DexShapeClassLoader`.

```text
Rust LoaderId(app)      -> Kotlin shape loader app
Rust LoaderId(plugin A) -> Kotlin shape loader plugin A
Rust LoaderId(plugin B) -> Kotlin shape loader plugin B
```

Parent relationships mirror `ClassLoaderGraph` where possible. Duplicate descriptors in different Rust loaders therefore become distinct JVM `Class<?>` objects in different Kotlin class loaders.

Framework/JDK classes come from configured jars such as Robolectric `android-all`; app/dex classes come from generated shape classes.

`DexShapeClassLoader` must define loader precedence explicitly:

- primitives and arrays use JVM semantics with component resolution through the same loader context
- app shape descriptors owned by the current `LoaderId` resolve in that shape loader before parent delegation when Android loader policy requires child-local lookup
- framework/JDK descriptors delegate to configured framework/JDK loaders
- duplicate app descriptors in different `LoaderId`s produce distinct `Class<?>` objects
- unresolved references may define lazily but must fail at the same verification/use boundary as old `SmaliClassLoader` tests expect

### Shape Class Protocol

Rust sends shape metadata lazily or in batches.

```text
define_shape_class(session, loader_id, class_shape) -> ClassToken
define_shape_batch(session, loader_id, class_shapes) -> Vec<ClassToken>
resolve_shape_class(session, loader_id, descriptor) -> ClassToken
```

The server may also need a class while executing framework code. It must not guess. It sends a reverse request to Rust:

```text
resolve_shape_from_rust(session, loader_id, binary_name, init_mode) -> ClassShape | NotFound
```

Rust resolves through the path's `LoaderSnapshot`, runs app class initialization in Rust only when `init_mode` requires it, and returns shape metadata. The Kotlin server then defines the shape in the matching `DexShapeClassLoader`.

```rust
enum ClassInitMode {
    LoadOnly,
    Link,
    Initialize,
}
```

`Class.forName(String)` uses `Initialize`. `Object.getClass` and normal type resolution use `LoadOnly` or `Link` as required.

For performance, Rust can eagerly publish shape batches for a loader after root APK load or runtime dex attach. Lazy reverse resolution is still required for dynamic class names and reflection-heavy code.

`ClassShape` includes:

```rust
struct ClassShape {
    descriptor: TypeDescriptor,
    access_flags: ClassAccessFlags,
    superclass: Option<TypeDescriptor>,
    interfaces: Vec<TypeDescriptor>,
    fields: Vec<FieldShape>,
    methods: Vec<MethodShape>,
    annotations: Vec<AnnotationShape>,
    source_file: Option<String>,
}
```

Generated classes are cached by `(SessionId, LoaderId, descriptor)`.

### Class and Mirror Values

`Class<?>` objects can cross the Rust/JVM boundary.

Wire values distinguish app shape classes from framework classes:

```rust
enum WireClassRef {
    AppShape { loader_id: LoaderId, descriptor: TypeDescriptor },
    Framework { binary_name: String },
    Primitive { descriptor: TypeDescriptor },
    Array { descriptor: TypeDescriptor },
}
```

When JVM server returns a `Class<?>` for an app shape class, Rust maps it to `Value::ClassMirror(ClassId)`, not to an opaque host object. When Rust passes that mirror back into JVM execution, the server reifies it through the matching `DexShapeClassLoader`.

Framework `Class<?>` values remain JVM-server values unless Rust has a reason to map them to framework metadata.

### App Object Crossing

Generated shape classes do not solve arbitrary app-object crossing by themselves.

If a framework method receives an app object, the server can only execute correctly if the object is materialized as a shape instance for that path/version.

V1 behavior:

- App `Class` mirrors can be reified as shape `Class<?>` objects.
- App object instances are not generically proxied into the JVM server.
- Method-specific adapters may materialize app object fields into a shape instance for known safe cases.
- After a JVM transaction, adapters must extract modified fields back into Rust state.
- Without an adapter, app-object arguments cause fallback/unknown behavior.

This avoids fake correctness. A shape class gives Java reflection a type. It does not automatically give Java code a coherent mutable app object.

Old smalivm had the same limitation. `java_lang_reflect_Field_get` handled local app static fields through `StaticFieldAccessor`, but returned unknown for local app instance fields. The Rust design should preserve that honest fallback until app-object materialization adapters exist.

Required bridge objects for app reflection:

- app `Class<?>` can be represented as a generated shape class
- app `Field` mirrors returned to JVM must be bridge-aware if `Field.get/set` targets Rust-owned state
- app `Method` mirrors returned to JVM must be bridge-aware if `Method.invoke` targets Rust-owned Dalvik execution

V1 may support app static `Field.get/set` through bridge handling and explicitly return unknown for app instance `Field.get/set`, matching the old implementation. Claiming full app reflection support requires materializing Rust object instances into shape instances and extracting field deltas back.

### Static Fields

App static fields are Rust-owned. Shape class static fields are only JVM materialization for a transaction.

If JVM code reflectively reads or writes an app static field, the transaction must:

- materialize the Rust static value into the shape class before invocation
- extract changed value after invocation
- commit the change to the Rust path state if policy allows

Without that materialization rule, shape class statics would silently diverge from Rust state.

### Object Construction

Generated shape constructors are not trusted app constructors.

Old `ObjectInstantiator` used `newInstance`, then fell back to picking a constructor and defaulting arguments, with special enum handling. That was pragmatic but leaky.

V1 rules:

- App/dex object allocation is Rust-owned.
- JVM shape instances are only transaction materializations or reflection carriers.
- Shape instance construction should prefer Objenesis/Unsafe allocation to avoid accidentally executing constructors or superclass side effects.
- Constructor execution for a shape class is allowed only through an explicit adapter policy.
- Enum shape support should copy old behavior: enough scaffolding for `values`, `valueOf`, and enum reflection, not app enum business logic.

### Implementation Tooling

Use ASM or Byte Buddy inside the Kotlin server. ASM is closest to old `ClassBuilder` and gives exact bytecode/classfile control. Byte Buddy may reduce boilerplate but must not hide classloader identity or synthetic field/method choices.

The generated class format should be deterministic and debuggable. Dump class bytes behind a debug flag.

## Old Resource Migration

Old files:

- `safe_classes.cfg`
- `safe_methods.cfg`
- `safe_framework_classes.cfg`
- `unsafe_methods.cfg`
- `immutable_classes.cfg`
- `framework_classes.cfg`
- `android-framework.jar`
- `android-local.jar`

New model:

- `safe_classes.cfg` becomes method/class policy entries for `JvmServerBackend`, `RustModelBackend`, or `StubBackend`.
- `safe_methods.cfg` becomes explicit allowlist entries with effect policy.
- `safe_framework_classes.cfg` becomes initial allowlist for JVM-server execution.
- `unsafe_methods.cfg` becomes deny rules, overriding broad allow rules.
- `immutable_classes.cfg` becomes optimization/type-hint data only. It must not imply no internal mutation because classes like `String` have lazy private caches.
- `framework_classes.cfg` is replaced by Android profile metadata plus classpath jars.
- `android-framework.jar` is replaced by Robolectric `android-all` or user-supplied framework implementation jars.
- `android-local.jar` behavior is rewritten as Kotlin server policies/models or Rust models.

The policy file should be typed, not line-oriented magic.

Example:

```toml
[[method]]
signature = "Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;"
backend = "jvm-server"
effects = ["receiver-mutates"]
unknown_args = "fallback"

[[method]]
signature = "Ljava/lang/System;->currentTimeMillis()J"
backend = "rust-model"
effects = []
value = "profile.time.current_millis"

[[method]]
signature = "Ljava/lang/System;->exit(I)V"
backend = "deny"
```

### `PolicyCompiler`

The first policy source should be generated from the old configs, not rewritten by hand from memory.

`PolicyCompiler` imports:

- class-level safe rules
- method-level safe rules
- safe framework class rules
- unsafe deny overrides
- immutable hints
- old emulated method list

Precedence:

1. explicit deny wins over everything
2. explicit Rust/Kotlin model wins over broad safe class
3. explicit method allow wins over class default
4. class allow applies only to methods with known effect policy
5. unknown args use method policy; absent policy means fallback

The runtime must emit coverage debt:

- method denied by policy
- method allowed but failed execution
- method missing from policy
- unknown argument prevented JVM execution
- host graph taint prevented JVM execution
- app-object adapter missing

This turns every lost concrete value into a ranked backlog instead of a silent unknown.

## Rust Heap and JVM Heap Interaction

Rust heap stores Dalvik/app objects. JVM server stores host/framework objects.

Straightforward conversions:

- Rust primitive -> JVM primitive
- Rust concrete string -> JVM `String`
- Rust array of concrete primitives/strings/host objects -> JVM array
- JVM primitive/string -> Rust value
- JVM object -> Rust `Value::HostObject`

Hard conversions:

- app-defined Rust object passed to JVM framework method
- JVM object passed to app method expecting direct field layout
- unknown values

V1 behavior:

- If framework method receives app-defined object, use method-specific adapter if available.
- Otherwise fallback to unknown/stub behavior.
- Do not create generic host proxies for arbitrary app objects in v1.

This is an explicit limitation and likely future work.

## Analysis Graph

The execution graph records states and events needed by `simplify`.

Per node:

- method id
- code address
- opcode
- input state summary
- output state summary
- register reads/writes
- heap reads/writes
- static reads/writes
- host JVM operations
- normal successors
- exceptional successors
- side effect level
- diagnostics

`AnalysisView` exposes stable queries scoped by code version, analysis epoch, node set, and state point. Object IDs alone are never enough for mutable/host state.

```rust
struct CodeVersionId(u64);
struct AnalysisEpochId(u64);
struct NodeSetId(u64);

enum StatePoint {
    Before,
    After,
}

enum NodeScope {
    Address { method: MethodId, address: CodeAddress },
    Parents { method: MethodId, address: CodeAddress },
    Terminating { method: MethodId },
    Explicit(NodeSetId),
}

trait AnalysisView {
    fn all_addresses(&self, method: MethodId, version: CodeVersionId) -> Vec<CodeAddress>;
    fn reached_addresses(&self, method: MethodId, epoch: AnalysisEpochId) -> Vec<CodeAddress>;
    fn instruction(&self, method: MethodId, address: CodeAddress, version: CodeVersionId) -> InstructionRef;
    fn try_blocks(&self, method: MethodId, version: CodeVersionId) -> Vec<TryBlockRef>;
    fn node_pile(&self, method: MethodId, address: CodeAddress, epoch: AnalysisEpochId) -> Vec<NodeId>;
    fn predecessors(&self, node: NodeId) -> Vec<NodeId>;
    fn successors(&self, node: NodeId) -> Vec<NodeId>;
    fn register_items(&self, scope: NodeScope, point: StatePoint, register: Register) -> Vec<ValueSummary>;
    fn register_consensus(&self, scope: NodeScope, point: StatePoint, register: Register) -> Consensus<ValueSummary>;
    fn field_consensus(&self, scope: NodeScope, point: StatePoint, field: FieldId) -> Consensus<ValueSummary>;
    fn ordered_accesses(&self, node: NodeId) -> Vec<AccessEvent>;
    fn call_summary_at(&self, node: NodeId) -> Option<CallSummary>;
    fn side_effect_at(&self, node: NodeId) -> SideEffectLevel;
    fn highest_side_effect(&self, method: MethodId, epoch: AnalysisEpochId) -> SideEffectLevel;
    fn host_object_summary(&self, node: NodeId, object: JvmObjectId) -> Option<HostObjectSummary>;
    fn host_field_consensus(&self, scope: NodeScope, object: JvmObjectId, field: HostFieldId) -> Consensus<ValueSummary>;
    fn mirror_summary(&self, value: &ValueSummary) -> Option<MirrorSummary>;
    fn host_events(&self, node: NodeId) -> Vec<HostEvent>;
}
```

`simplify` should optimize through `AnalysisView`, not by depending on VM internals.

Code rewrites produce new `CodeVersionId`s and new `AnalysisEpochId`s. Old graph facts must not be reused against rewritten code unless explicitly mapped by a rewrite plan.

```rust
struct RewritePlan { /* add/remove/replace instructions, labels, try blocks, register growth */ }
struct RewriteResult { version: CodeVersionId, invalidated_epochs: Vec<AnalysisEpochId> }
```

Future `simplify` strategies either emit a `RewritePlan` or call a rewrite session API that produces one. After rewrite, affected methods are re-executed/reanalyzed into a fresh epoch.

Graph nodes must record enough host results for later optimization without requiring every historical JVM version to stay live forever. During execution, live frontier states retain `JvmVersionId`s. When nodes become analysis history, the engine stores serialized host summaries for values used by optimization, such as strings, arrays, primitive fields, JSON `toString`, and diagnostic host events. Full JVM versions may be garbage-collected after summaries are persisted and no frontier state references them.

Persisted host summaries are scoped to node/path/version, not only object ID. Required summaries include:

- concrete string contents and `toString` results
- primitive arrays and object arrays: length, element summaries, type
- class mirrors: descriptor, loader ID, app/framework ownership
- method mirrors: declaring class, name, proto, access flags, static/private/interface/constructor bits, `accessible`
- field mirrors: declaring class, name, type, access flags, static bit, `accessible`
- mutable host object summaries for supported value classes such as `StringBuilder`, collections, `JSONObject`
- taint/unknown reason when not concrete

If a query exists in `AnalysisView`, the persisted summary must be sufficient to answer it after the full JVM version has been dropped.

## Dynamic Class Loading

Dalvik-side class loaders are modeled in Rust.

Important methods:

- `dalvik.system.DexClassLoader.<init>`
- `dalvik.system.PathClassLoader.<init>`
- `dalvik.system.InMemoryDexClassLoader.<init>`
- `dalvik.system.DexFile.loadDex`
- `java.lang.ClassLoader.loadClass`
- `java.lang.Class.forName`

If loader input is concrete, Rust attaches a new dex unit to the appropriate loader. If input is unknown, Rust records unknown loader state and future resolution becomes conservative.

JVM server class loading is separate. It loads framework/JDK jars for framework execution. It must not become the owner of app dex class identity.

When Rust attaches a runtime dex unit, it also updates shape-class availability:

- create or reuse the matching Kotlin `DexShapeClassLoader` for the Rust `LoaderId`
- mirror parent loader relationships
- publish or lazily expose new `ClassShape`s from that dex unit
- invalidate negative shape lookup caches for that loader

This keeps `ClassLoaderGraph` and JVM reflection shape in sync without letting Kotlin decide app dex identity.

## Reflection

Reflection crosses both worlds.

Rust owns reflection over app/dex classes:

- app `Class` mirrors
- app `Method` mirrors
- app `Field` mirrors
- dynamic dex loader resolution

Kotlin JVM server owns reflection over JVM-server classes/objects:

- framework/JDK class mirrors
- host object private fields
- host method invocation

When reflection target is ambiguous or crosses worlds, policy decides. V1 should prefer conservative unknown over fake proxy magic.

Reference behavior to preserve:

- `Class.forName` for safe/JDK classes returns real JVM classes.
- `Class.forName` for app/dex classes returns generated shape classes and initializes app class state in Rust.
- `Object.getClass` for app objects returns the generated shape class for the object's Rust type.
- `Field.get` for safe/JVM fields reads real JVM fields.
- `Field.get` for app static fields reads Rust `StaticSnapshot` through bridge handling.
- `Field.get` for app instance fields returns unknown until explicit materialization adapters exist.

Reflection must preserve Java access checks:

- caller class/package/nesting/superclass relationships matter
- public/protected/package/private access rules are enforced
- `setAccessible(true)` is tracked on `Field`, `Method`, and `Constructor` mirrors
- inaccessible access produces structured `IllegalAccessException`, not unknown
- app instance `Field.get` still performs access checks before returning typed unknown for the value
- `ClassNotFoundException`, `NoSuchFieldException`, and `NoSuchMethodException` are explicit exceptional results where Java would throw them

Reflection handles carry loader identity, declaring class, descriptor/proto, owner world, and accessibility state. A string signature alone is never enough.

## Android/JDK Runtime Profile

The Kotlin server runs on a host JVM. That does not magically make host `java.*` match Android/libcore.

Old `SmaliClassLoader` explicitly refused to load missing `java.*` Android-only classes because protected packages are painful. The new design must make this explicit.

`AndroidProfile` defines:

- framework implementation jars, usually Robolectric `android-all`
- host JDK version
- Android API level
- method/class policy imports from old configs
- Java/libcore divergences handled by Kotlin shims or Rust models
- denied Android-only `java.*` APIs
- deterministic device/system values

Rules:

- host JVM bootstrap classes remain host JVM classes
- Android framework classes come from configured jars where classloader rules allow
- Android/libcore-specific `java.*` behavior requires explicit shim/model policy
- unsupported Android-only protected-package classes fall back to unknown/denied behavior, not fake classes in `java.*`

This is not as complete as a real ART runtime. It is the same tradeoff old smalivm made, but named and testable.

## Performance Strategy

Correctness first.

V1 JVM server clone strategy may be slow:

- reflection deep clone per write transaction
- immutable committed heap versions
- explicit `retain_versions` to collect dead versions

Expected optimizations after correctness:

- clone only reachable graph from receiver/args/statics touched by transaction, but only with alias-closure preservation proven by tests
- copy-on-write object maps inside server
- class-specific fast clone adapters for hot classes
- method result cache for pure methods
- message batching, signature interning, and compact binary payloads
- batching multiple field reads/writes in one transaction

The architecture must not depend on these optimizations for correctness.

### Host Version Retention

Version liveness is owned by Rust.

- frontier states retain their current `HostHeapRef`
- pending transactions retain their base version and temporary clone
- graph history stores serialized host summaries rather than requiring full heap versions forever
- `retain_versions` receives the exact live version set after each scheduler epoch
- dropping a version never invalidates committed `JvmObjectId`s in summaries, but it does make that version unavailable for future mutation

If an analysis query requires a full host version that has been dropped, that is a bug in retention policy or query design. The query API should prefer summaries unless mutation/re-execution is required.

## Failure Modes

Explicit errors are better than silent unknowns.

Representative errors:

```rust
enum FrameworkError {
    SemanticFallback(SemanticFallback),
    InfrastructureFailure(InfrastructureFailure),
}

enum SemanticFallback {
    UnsupportedClass(ClassRef),
    UnsupportedMethod(MethodRef),
    UnsupportedField(FieldRef),
    UnknownArgument { index: usize, ty: TypeDescriptor, source: UnknownSource },
    DeniedByPolicy(PolicyRuleId),
    NativeDenied(MethodRef),
}

enum InfrastructureFailure {
    Timeout,
    ServerCrashed,
    SerializationError(String),
    CloneError(String),
    ProtocolError(String),
}
```

Permissive mode converts semantic fallbacks to unknown/fallback effects. Infrastructure failures abort the current backend operation and may degrade to unknown only if policy explicitly allows losing JVM precision for that call.

## Testing Strategy

Behavioral tests only matter here.

The smalivm reference tests are not optional. They are the executable migration contract. If the Rust port gets the high-level architecture and this test translation layer right, opcode mechanics and later `simplify` work become tractable. If test semantics drift, the port is worthless even if individual opcode code looks clean.

### Reference Test Surface

Existing smalivm tests cover these behavior families:

- opcode semantics: const, move, unary/binary math, cmp, if, switch, payloads, arrays, fields, casts, instanceof, new-instance, invoke, throw, monitor ops
- execution graph behavior: reached addresses, node piles, terminating consensus, side effects
- state behavior: register setup, class fields, unknown equality, arrays, mutable parameters, aliasing
- invocation: local calls, virtual dispatch, constructors, mutable argument propagation, callee multiverse collapse, static field mutation
- exceptions: handler resolution, normal+exceptional path coexistence, always-throw paths, exception register/throw register state
- static/class behavior: class initialization, field inheritance, method inheritance, side effects
- framework/JVM bridge behavior: method reflection, `Class.forName`, `Field.get`, generated class shapes, enum helpers, hash code behavior
- configuration behavior: safe/unsafe/immutable class lists and loadability
- type behavior: arrays, ancestors, class manager lookups

Fixtures live under `smalivm/src/test/resources/smali`. Day-one Rust does not need user-facing smali input, but tests still need those behaviors. Use a pinned fixture pipeline:

- compile smali fixtures to dex at build-time or check in compiled dex artifacts
- record a machine-readable provenance manifest: old test name, smali path, method descriptor, compiled dex hash, expected address map, suite tag
- fail CI if source fixture and compiled artifact drift
- avoid runtime smali compilation in normal test runs
- preserve stable code addresses for tests that assert visitation

Test convenience must not drive product input scope.

### Test Harness Contract

Old `VMTester`/`Tester` define the semantic assertions that must survive the port. They do not define the Rust vocabulary.

The Rust harness should use idiomatic Rust names and concepts from this design: `ExecutionGraph`, `AnalysisView`, `TestState`, `Value`, `Register`, `ClassId`, `FieldId`, `CodeAddress`, `Consensus`, and host summaries.

The goal is tiny tests. Each test should express intent in a few lines: setup, execute, assert. Boilerplate belongs in fixtures, builders, and assertion helpers.

Example shape:

```rust
let graph = fixture("ops/invoke_static_test").execute("invokeMutateStringBuilder()V", state! {
    r0 = string_builder("duty is heavy as a mountain")
});

graph.assert_return_state(state! {
    r0 = string_builder("duty is heavy as a mountain, death as light as a feather")
});
```

Possible helper vocabulary:

```rust
fixture("ops/if_test").execute("ifUnknown()V", state! { r0 = unknown("I") });
graph.assert_reached([0, 2, 4]);
graph.assert_register(RESULT, value!(7, "I"));
graph.assert_field("Lfoo;->bar:I", value!(3, "I"));
graph.assert_exception::<NullPointerException>();
graph.assert_consensus_unknown(r0, "I");
```

Names are illustrative, not final API. The contract is compact, idiomatic tests with no repeated VM setup noise.

Assertions must preserve old comparison semantics:

- expected unknown compares by type/string form, not object identity
- arrays compare by element contents and array type
- `StringBuilder`/host mutable values compare by semantic contents where old tests did
- null values compare as typed nulls
- register assertions use terminating register consensus
- field assertions use terminating field consensus
- missing consensus is failure unless expected unknown says otherwise

This harness is an API design tool. If `AnalysisView` cannot implement these helpers cleanly, `AnalysisView` is wrong.

Test helpers should live in a dedicated test support crate/module, not inside production VM APIs. Production names stay clean; tests get ergonomic macros/builders.

### Porting Strategy

Port tests in layers, preserving intent rather than Java/JUnit syntax.

1. Build test fixture pipeline.
   - Compile existing smali fixtures to dex for Rust tests.
   - Keep fixture names and method descriptors stable.
   - Record fixture provenance so failures point back to old tests.

2. Port harness semantics before opcode details.
   - `TestState`
   - fixture builder
   - compact execution helper
   - terminating register consensus assertions
   - terminating field consensus assertions
   - visited-address assertions
   - exception assertions
   - macros/builders for `unknown`, typed values, arrays, host objects, and expected fields
   - node pile count assertions
   - alias assertions
   - ordered read/write assertions
   - side-effect assertions
   - policy-debt assertions
   - no-write-on-exception assertions

3. Port opcode tests mechanically.
   - One Rust module per opcode family.
   - Preserve initial/expected state tables.
   - Preserve edge cases involving unknown values, nulls, primitive width, and arrays.

4. Port VM architecture tests early.
   - invocation and multiverse collapse
   - mutable parameter propagation
   - static field mutation
   - class initialization
   - exception routing
   - graph consensus and side effects

5. Port JVM bridge tests as integration tests.
   - Kotlin server process lifecycle
   - generated shape classes
   - `Class.forName`
   - `Field.get`
   - safe reflection
   - host heap versioning and alias preservation

6. Port config tests through `PolicyCompiler`.
   - old safe/unsafe/immutable configs import successfully
   - deny overrides allow
   - immutable hints do not suppress internal lazy mutation
   - missing semantic coverage emits policy debt

### Test Classification

Tests should be tagged by architectural dependency:

- `core-state`: no dex parser, no Kotlin server; tests `State`, heap, aliases, consensus
- `opcode-unit`: one opcode handler with synthetic decoded instruction
- `dex-exec`: compiled dex fixture through full Rust VM
- `jvm-backend`: Kotlin server and versioned JVM heap
- `shape-class`: generated JVM class shape/reflection
- `policy`: config import and allow/deny behavior
- `corpus`: larger end-to-end fixtures and malware-like samples

This prevents slow JVM backend tests from blocking fast opcode/state iteration while still making JVM behavior mandatory in CI.

### Required Regression Tests From Reference Bugs

These tests must exist before implementation is called architecture-complete:

- multiple caller registers alias the same mutable object; mutating through one updates the alias in that path
- object graph alias closure: `A.b == B` remains true after JVM transaction clones/mutates `A` or `B`
- nondeterministic callee mutation collapses mutable argument to unknown
- deterministic callee mutation propagates concrete argument/static changes
- app `Class.forName` returns a shape class and initializes Rust-owned class state
- app static `Field.get` reads Rust `StaticSnapshot`
- app instance `Field.get` returns unknown unless adapter exists
- generated enum shape supports `values`/`valueOf` enough for old enum tests
- `String.hashCode` or equivalent lazy private cache mutation is visible in JVM backend version state
- always-thrown call removes normal successors
- sometimes-thrown call records both normal and exceptional paths
- catch resolution picks most-specific typed handler before catch-all
- unknown branch predicate creates both successor paths

### Acceptance Gate

The Rust smalivm port is not allowed to claim parity until:

- every behavior in active, disabled, commented, and fixture-backed smalivm reference tests has a Rust/Kotlin equivalent, or is explicitly documented as intentionally dropped with product reason
- every intentionally dropped test is reviewed against product goals, not convenience
- all ported tests pass under the new architecture
- `AnalysisView` supports the Rust test harness assertions without exposing executor internals
- policy debt reports are generated for every fallback/unknown caused by missing framework/JDK semantics
- simplify strategy tests and `ExecutionGraphManipulator` behavior are included before claiming `simplify` readiness

Suite gates:

- `core-state`, `opcode-unit`, and core `dex-exec` run on every normal test invocation
- `jvm-backend`, `shape-class`, and `policy` are mandatory integration tests, isolated from fast unit loops but still CI-gated
- `corpus` can be slower/nightly/full-suite, but failures are release blockers

Benchmarks and parser convenience are secondary. Semantic regression tests are the migration spine.

Core corpus:

- opcode execution and exceptions
- branch fork with object mutation
- root multidex loading
- runtime dynamic dex attach
- JVM server `JSONObject` fork
- JVM server static field fork
- JVM server private field reflection
- unknown argument fallback
- timeout/denied method fallback
- old `safe_*` config migration cases

Golden examples:

```java
JSONObject o = new JSONObject();
if (unknown()) {
    o.put("a", 1);
} else {
    o.put("b", 2);
}
return o.toString();
```

Expected: two paths, two JVM heap versions, divergent concrete strings.

```java
class C { static int x; }
if (unknown()) C.x = 1; else C.x = 2;
return C.x;
```

Expected: static field is versioned by path.

```java
class Box { Object value; }
Box a = new Box();
Box b = new Box();
a.value = b;
if (unknown()) {
    mutateThroughA(a); // mutates ((Box)a.value)
} else {
    mutateDirectly(b);
}
return a.value == b;
```

Expected: alias identity is preserved inside each path. Mutating `b` through `a.value` and reading `b` directly observe the same versioned object for that path. Other paths retain prior versions.

## Remaining Validation Spikes

Before implementation planning, validate the JVM server design.

1. Kotlin JVM heap version spike
   - Create `JSONObject`.
   - Fork snapshot.
   - Path A `put("a", 1)`.
   - Path B `put("b", 2)`.
   - `toString` diverges.
   - Preserve object ID across versions.

2. Static field version spike
   - Class with mutable static field.
   - Fork before mutation.
   - Mutate static differently in each path.
   - Reads diverge.

3. Reflection/private field spike
   - Read/write private fields through server protocol.
   - Confirm lazy `String.hash` or equivalent private cache behavior.

4. Protocol spike
   - Length-prefixed binary frames over Unix socket.
   - `begin_tx`, `invoke`, `commit_tx`, `get_field`.
   - Timeout and error propagation.

5. Dex backend spike
   - Confirm parser exposes all execution metadata.

6. Dynamic dex loader spike
   - Attach plugin dex at runtime and resolve duplicate descriptors by loader.

7. Safe surface audit
   - Convert old configs into ranked policy backlog.

## Open Questions

- Which deep clone implementation is best for Kotlin server v1: reflection-only, Objenesis/Kryo, or custom hybrid?
- How much app-object proxying is needed for real malware/framework interactions?
- Which Android framework jar/profile should be default: Robolectric `android-all`, user-supplied jar, or both?
- How should Rust and server coordinate class initialization for framework classes that have static side effects?
- What is the first policy format: TOML, protobuf text format, or generated Rust/Kotlin tables?
- How large can the versioned JVM heap get on representative deobfuscation samples?

## Recommendation

Use this architecture:

- Rust VM for Dalvik execution, graph recording, class loader graph, dynamic dex, exceptions, and analysis API.
- Kotlin JVM backend for allowlisted framework/JDK semantic execution with versioned heap snapshots.
- Typed policy layer migrated from old `safe_*` configs.
- Conservative fallbacks for unknowns, unsupported natives, app-object crossing, and dangerous side effects.

This keeps the Rust VM clean while preserving the main reason old Java `smalivm` worked: real JVM-shaped objects can behave like real JVM objects across reflection, private fields, lazy caches, and framework utility code.
