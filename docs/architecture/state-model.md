# State and Value Model

## Clean-core state contract

`OpExecution` exposes independent `OpArm` values. The scheduler copies persistent frame roots per arm, applies
only that arm's `OpUpdate`, and reconciles only the named footprint for the source-node summary. Fresh throwable
recipes materialize after the arm copy, so normal siblings never inherit exceptional allocations.

`FrameValue` is the internal atomic register and slot cell. It contains one public `Value` projection and,
when present, one `ThrowableDomain`. Persistent frame copies share that carrier. Move-object, move-exception,
check-cast, guest argument binding, guest return, move-result, and rethrow preserve it. Local routes, caller
routes, and unhandled paths carry a `ThrownSubject`; no second domain parameter can disagree with it.

Current path constraints are never ignored inside this tracked transport. These explicit abstraction boundaries
may conservatively forget a refinement and widen later handler routing:

- tracked array elements;
- tracked instance fields;
- static fields;
- accepted host calls;
- opaque calls;
- public APIs that project a `FrameValue` to `Value`.

These boundaries preserve the underlying `Value` and concrete `HeapRef` identity. They discard only the
internal residual-domain fact. No compatibility refinement map exists beside the atomic carrier.

`VmServices.newSession` also sanitizes a caller-supplied public entry frame. It copies the frame and strips
internal refinements from registers and special slots. Reusing that frame cannot transfer hidden path evidence
between sessions. Internal activation and test construction keep refinements; sanitization is not a general
`Frame.copy()` rule.

Class initialization uses internal lifecycle records while `ClassInitState` remains the public projection.
`Failed` stores the first outward `ThrownSubject`; this value participates in persistent copies, compatibility
keys, guest-root retention, and host selection. `ClassStore` owns validated transitions. A
`ClassInitContext` identifies the active initializer and ordered dependents that wait for it. If the initializer
fails, one outward failure is stored for the active class and every waiting dependent before routing continues.
A later trigger allocates a fresh `NoClassDefFoundError` caused by the stored outward failure.

## Persistent state and host identity

- `Frame.copy()`, `HeapStore.copy()`, and `ClassStore.copy()` create path-owned wrappers over
  persistent roots without traversing live register, heap, class, or static entries. Later writes
  replace only the writing path's roots; an unchanged successor does not clone the heap.
- Observations such as `Frame.peekRegister`, `heapObject`, `ClassStore.initState`, and
  `getStaticField` read existing state without creating execution-state versions or clone-on-read
  cache entries. Instruction read bookkeeping is distinct from debugger observation.
- Mutable host objects belong behind `HeapRef` identities in one path-local `HostGraphState`.
  A selected, accepted synchronous backend detaches that graph before receiving executable Java
  objects. `cloneHostGraph` uses one identity map across all roots to preserve nested aliases
  and cycles; sibling frame roots are not exposed for host mutation.
- `HeapStore.finalizeHostGraph` compares every object the detached graph held with its clone, paired by node
  id before pruning, so objects the call only added are not changes. Only `Equal` can restore the original graph
  root, and restoration is suppressed when dirty array mirrors were synchronized or pruning changed the node
  layout. `Different` or `Unprovable` never means unchanged; unprovable connectivity can invalidate host
  contents. The scheduler takes the call's effect from the comparison: `Different` is `WEAK`, `Unprovable` is
  `STRONG`.
- `SelectedHostCall.finalize` validates array mirrors before atomic lattice copy-back and finalizes
  graph comparison even if nonfatal copy-back fails. Returns, target throws, and ordinary backend
  failures pass through selected-call finalization. `VirtualMachineError` is deliberately rethrown
  rather than treated as a recoverable result.

These contracts do not claim whole-JVM isolation: host execution remains subject to the configured
backend and reflection policy.
