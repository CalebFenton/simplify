# Exception Handling

## Throwable transport and routing

The execution engine uses one resolver for local throws, caller throws, and accepted target throws. A normalized
`ThrownSubject` owns one atomic `FrameValue`: the public `Value` and its current `ThrowableDomain` cannot move
separately. Raw host throwables use `virtualTypeOfHostClass(raw.javaClass)`; a loose `Value.type` tag is never the
runtime-type authority. An imported throwable also carries immutable ancestry facts read from that exact JVM
class. The shared resolver uses those facts only for that throw event; `ClassIndex` does not load or cache host
classes for unrelated guest descriptors, so missing-descriptor ambiguity remains intact. A known host object whose JVM value
is not a `Throwable` instead becomes one fresh guest `InternalError`, even when its descriptor is absent.

The resolver applies first-match order and carries the residual domain on each route. [JVMS 2.10](https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-2.html#jvms-2.10)
defines ordered exception-table search, and [JVMS `athrow`](https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-6.html#jvms-6.5.athrow)
requires the first matching handler. Android's [DEX `try_item`](https://source.android.com/docs/core/runtime/dex-format#try-item)
defines start plus instruction count, requires non-overlap, and orders valid ranges by address. Thus ranges are
half-open. Distinct overlap is malformed metadata, not nearest-range behavior.

An absent catch or thrown descriptor is not a negative subtype fact. The analyzer keeps the matching route and
the residual escape because neither is disproved. `ThrowableHierarchy` provides cached tri-state facts without
using the throwing Boolean hierarchy for absent descriptors. Unexpected resolver failures use the same path-local
host containment for local and caller lookup. `VirtualMachineError` and scheduler invariants remain loud.

Accepted reflection and emulator target throws use this same guest resolver. Infrastructure failures do not:
they end only the affected path as `HOST_FAILURE`, preserve older siblings, and make completion `Partial`.
Backend-declared effects are recorded before execution, so neither target routing nor infrastructure failure can
erase them. Guest routing itself adds no effect. A `VirtualMachineError` returned as `TargetThrew` escapes by
identity before host-graph finalization, import, or routing. Newly returned and projected host throwables use the
same recursive identity-map importer as target throws, preserving causes, null terminators, cycles, and host
materialization.

The scheduler partitions caller throws by source key before handler resolution:

```text
concrete: (Value.Known, unresolved ThrowableDomain constraints, CallCompatibilityKey)
symbolic: (ThrowableDomain, CallCompatibilityKey)
CallCompatibilityKey: (class-init compatibility, tracked-field compatibility)
```

A concrete key uses the original `Value.Known`, including its `HeapRef` and public type tag. Fully known domains
for the same concrete value form one source because the merged heap supplies their runtime type. Handler
addresses and route refinements are not source-key fields.

Each source key merges callee state once into the suspended caller frame. Caller registers remain unchanged.
The merged heap supplies a concrete throwable's runtime type without replacing its `Value` tag. Concrete
required/excluded partitions and symbolic domains remain attached to their `FrameValue`. The shared resolver then
runs once at the caller's invoke address.

Routing repeats that result for each original exit, which preserves path multiplicity without repeated merges
or lookups. Each exit's selected handlers and residual escape remain one `ALL_OR_NOTHING` group. A malformed target
commits no sibling route. Residual escapes enter the parent completion and cross the next boundary only after it
drains.

An escaping class-initializer throw is transformed before handler lookup. An `Error` keeps its identity; any
other throwable becomes the cause of one fresh `ExceptionInInitializerError`. The active class and its waiting
dependents store that outward failure before the shared resolver routes it. A later trigger does not rerun the
initializer. It throws one fresh `NoClassDefFoundError` named by the class's binary name whose cause is the stored
outward failure; a trigger whose superclass failed throws that superclass's error instead. `ClassInit.kt` owns this
decision and builds the throwable for every trigger: ops, invocations, and emulators. A declared `<clinit>` with no
code ends the trigger's path as `MALFORMED_INPUT`. Analyzer limits, unsupported operations, and host failures
remain `Partial` and do not create failed-class state.

The resolver has no handler cache and no exception path cap.
