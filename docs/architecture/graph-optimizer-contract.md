# Execution Graph and Optimizer Contract

## Editor contract

SmaliVM separates method structure, execution evidence, and editing:

- `ClassIndex.methodView(signature)` returns the current immutable method for a `MethodSignature`, or null
  when the program has no such method. `MethodSignature.parse` reads a signature from the smali spelling
  `Lpkg/Owner;->name(params)return`.
  `VmServices.newSession(view)` captures the entire program revision, including future callees and
  class initializers. A preexisting session does not switch revisions when an edit commits.
- A default constructor session starts with a fresh uninitialized receiver and declared instance-field
  defaults, including inherited fields, when the field layout resolves. Other parameters remain unknown.
  An explicit entry frame retains the caller's supplied receiver; session execution cannot mutate that frame.
- `SmaliClassLoader` preserves the executable Base64 classes compiled from pinned AOSP source instead
  of replacing them with generated framework stubs. Other framework classes retain Android metadata
  shapes. Class selection does not grant host execution permission; reflection policy still applies.
- `ExecutionGraph.trusted()` returns analysis evidence only after complete execution.
  Optimizations must start with `services.methodEditor.beginEdit(trustedGraph)`: this rejects evidence
  from an older program revision, even if another method was the one changed.
- `beginEdit(methodView)` supports structural edits without analysis evidence. It guards the method
  revision, not whole-program evidence; it is not permission to optimize from a partial or stale graph.
- `MethodEditTransaction` stages insertion, replacement, removal, try regions, scratch registers,
  control targets, and DEX references. Addresses refer to its original method body; control targets use
  transaction-owned `PendingCodeAnchor` values.
- `commit()` returns `Committed`, `Unchanged`, or `Rejected`. Rejection and `discard()` publish nothing.
  `commit()` is the only validation gate: a `Rejected` reason names the rule the edit breaks, and
  publication rechecks only snapshot identity and revision order.
  A committed result supplies the new `editedMethodView`. Re-execute it before another analysis-based
  rewrite. Existing graphs retain their original method structure and results.
- `ProgramDexWriter.write(services.currentSnapshot, outputFile)` serializes the same code that fresh
  sessions execute. Output includes all application classes and methods, not only optimization targets,
  and excludes bundled framework classes. An older retained snapshot can still be serialized separately.
- Simplify's Kotlin `Optimizer` runs constant propagation, dead code removal, peephole optimization,
  and unreflection through `StrategyInput`. Each strategy receives the trusted graph of a complete run, the
  method's `MethodControlFlow`, and a fresh transaction the optimizer opened. After a commit, the optimizer
  re-executes the published method and restarts at the first strategy.
  Only committed changes contribute to optimization counts. Partial analysis authorizes no rewrite.
- Effect and identity proofs remain conservative: volatile accesses, writes to kept arrays, synchronization,
  and observable exceptions are retained. Reflective calls require proven normal continuation and no side
  effects before lowering.
- Constant propagation folds a known `String` into a `const-string`, whose interned object is not the
  computed one, only where the method does not observe the object's identity. The object is followed
  from the folded register through `move-object*` copies and `check-cast` over every edge of the
  method's `MethodControlFlow`, taken or not, until each register holding it is redefined; it is
  observed when it reaches a two-register `if-eq`/`if-ne` or a `monitor-enter`/`monitor-exit`.
  `if-eqz`/`if-nez` test only for null. Identity inside callees, fields, arrays and returned values is
  treated as unobserved. A copy whose source provably holds the same `const-string` on every path
  folds regardless. A `String` constructor is kept and followed by the literal, and only when a later
  instruction still reads the object it built.
- Dead removal deletes a dead assignment, an unused call result, or a void call other than a constructor only
  when every structurally reachable instruction ran, the op continued normally on every execution and its
  effect is `NONE`. A constructor call goes only with its allocation.
- **Dead allocations.** An object the method allocates (`new-instance`, `new-array`, `filled-new-array*`)
  whose identity and state no kept instruction observes is removed with every instruction whose only purpose
  is that object. A forward pass over the static control flow records which allocations each register may
  hold and whether it may hold anything else; exceptional edges carry the pre-instruction state. A reader of
  an allocation goes with it only when every allocation it reads is dead, it continued normally on every
  execution, and it is one of:
  - the allocation itself, with effect `NONE` (an allocation that triggers an effectful class
    initialization stays);
  - a `move-object` or `check-cast` whose register holds only allocations, with effect `NONE`;
  - a `fill-array-data` or `aput*` into an array register that holds only allocations, with effect at most
    `WEAK`; an `aput-object` value must be dead or an immutable host value;
  - a confined call: effect `NONE` and not a constructor; or effect `NONE`, or `WEAK` where every execution
    ran a policy-authorized host method (`didEveryExecutionRunHostMethod`), with every reference operand
    dead or an immutable host value. A constructor's receiver must hold only allocations. A host method
    changes nothing outside the objects reachable from its receiver and arguments, so such a call changes
    only dead objects. `Class.forName` is never confined.

  Every other reader keeps each allocation it reads: a field or static store, a store into a kept array,
  `return`, `throw`, a reference comparison, `monitor-*`, `instance-of`, `array-length`, a field or element
  read, a `check-cast` that can throw, and a call that is not confined. A call result carries every
  allocation its operands hold, so a kept reader of the result keeps the call. A `move-result-object` that
  holds, on every execution, the one tracked object an operand holds (a builder returning `this`) may hold
  another value only where that operand may. A reader the liveness pass removes in the same pass, such as an
  unused `instance-of`, still keeps its allocations; the optimizer's next pass removes them.

## Analysis query contract

`ExecutionGraph` records the nodes each explored path produced. `ExecutionGraph.trusted()` is the public query
surface. Inside SmaliVM, `ExecutionGraph.explored` answers the same `ExploredPaths` queries without the
completion gate, for the debugger and tests; every rule below holds for both.

- **Completion-gated trust.** A consumer acts on an answer only from a `TrustedGraphView`. `trusted()`
  returns one only when `completion()` is `canTrustConsensus`: every path ran to an end and none aborted.
  A run stopped by a bound, an unsupported opcode, malformed input or a host failure is `Partial`; a
  mid-run snapshot is `InProgress`; both answer null. `explored` answers with no gate and describes only
  the walked paths: sound to read for a debugger or a test, never a licence to rewrite.
- **Dead code.** `TrustedGraphView.isDead(address)` is the only dead-code proof: true for an executable
  instruction no path reached. The internal `ExecutionGraph.wasReached` is ungated coverage; its `false`
  proves nothing.
- **Payloads.** `.array-data`, `packed-switch-data` and `sparse-switch-data` are data that control flow
  never reaches, so they are never reached on any run and `isDead` is false for them.
- **Post-op state.** The state at an address is the state after its op ran; consensus at an op's address
  is the value it produced. When the op destroys or never computes the needed value (a `check-cast`
  recast, an unemulated `Method.invoke`), read its parents through `parentAddresses` and the
  multi-address overloads. Only nodes whose op ran vote; a node a bound stopped stays in `nodePile`.
- **Per-address scope.** `consensus`, `registerValues`, `registerOccupancy`, the reflected-member queries
  and `sideEffectLevel` union every activation of the method, recursive ones included, because a rewrite
  at an address must hold on every activation that executes it.
- **Terminating scope.** `terminatingConsensus`, `terminatingRegisterOccupancy`, `didAnyPathReturn`,
  `terminatingStaticFieldConsensus` and `terminatingHeapObject` read only the paths that returned in the
  run's own activation; a callee's or a recursive activation's return does not vote, nor does a throwing
  or aborted path. `terminatingConsensus` agreeing on a ref says nothing about the object's contents;
  `terminatingHeapObject` joins those element by element.
- **No absence in band.** `reflectedMethod`, `reflectedField`, `arrayLength`, `arrayElementClasses`,
  `knownString` and `terminatingHeapObject` return an `Agreement`: `Agreed(value)`, `Disagree`, `Absent` (a path
  holds no value of that kind) or `NoPath` (no op ran there, or no path returned).
- **`Value.Top` pairings.** A `Top` consensus is read with `RegisterOccupancy`: `HELD` means live and
  unknowable; `NEVER_ASSIGNED` means no path holds a readable value, which is not "free" (the high half of
  a live wide pair also answers it; `availableRegisters` answers "free"); `NO_PATH` means no op ran there,
  or for the terminating query no path returned.
- **Effects.** `sideEffectLevel(address)` is the most any op there declared, or `STRONG` where a path
  aborted on an unsupported opcode. It is `NONE` for an address no op ran at. `methodSideEffectLevel` is
  the maximum over the method.
  `didEveryExecutionRunHostMethod(address)` is true only when every node there ran its call in the
  reflection backend: a host method the reflection policy authorized, which changes nothing outside the
  objects reachable from its receiver and arguments. Guest bytecode, emulators and opaque calls answer false.
- **Normal continuation.** `didEveryExecutionContinueNormally(address)` is true only when every node there
  ran and continued at a following instruction with no exceptional partition. A node that triggered class
  initialization continues normally when the initialization settles and its op runs again, and
  exceptionally when the initializer throws or aborts or a failed dependency throws.

## Demo and debugger consumers

- `demoapp` uses `VmServices`, entry frames, and per-VM emulator registration. Its print emulator
  declares a strong host side effect. Its exact reflection policy is local to the demo; it does not
  weaken general-purpose reflection defaults. Return consensus is joined from normal outcomes only
  when execution is complete.
- SDBG wraps one `ExecutionSession` with `core.Debugger`. Its cursor and register/field inspection
  describe the next pre-op frame. `stepInto`, `stepOver`, and `runToBreakpoint` drive that same session.
- SDBG resolves both application and framework methods through `ClassIndex.lookupMethod` and
  `MethodLookup.Runnable.view`. Application-only `ClassIndex.methodView` cannot resolve a framework callee.
- Disassembly uses public Baksmali APIs, not reflective access to its private writers. Application
  classes come from one lazily serialized immutable snapshot. `FrameworkCatalog.classDefinition`
  exposes cached immutable framework source for disassembly without exposing a mutable builder.
  Executable indexes and current/breakpoint markers come from `MethodView`; payloads are not numbered.
