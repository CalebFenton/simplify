# Execution Graph and Optimizer Contract

## Kotlin editor contract — V1 continuation

The resumed Kotlin core separates method structure, execution evidence, and editing:

- `ClassIndex.requireMethodView(className, methodDescriptor)` returns the current immutable method.
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
  transaction-owned `PendingCodeAnchor` values, not mutable `MethodLocation` objects.
- `commit()` returns `Committed`, `Unchanged`, or `Rejected`. Rejection and `discard()` publish nothing.
  A committed result supplies the new `editedMethodView`. Re-execute it before another analysis-based
  rewrite. Existing graphs retain their original method structure and results.
- `ProgramDexWriter.write(services.currentSnapshot, outputFile)` serializes the same code that fresh
  sessions execute. Output includes all application classes and methods, not only optimization targets,
  and excludes bundled framework classes. An older retained snapshot can still be serialized separately.
- Simplify's Kotlin `Optimizer` runs constant propagation, dead code removal, peephole optimization,
  and unreflection through `StrategyInput`. Each strategy receives complete evidence and a fresh
  transaction. After a commit, the optimizer re-executes the published method before the next strategy.
  Only committed changes contribute to optimization counts. Partial analysis authorizes no rewrite.
- Effect and identity proofs remain conservative: volatile accesses, array writes, synchronization,
  and observable exceptions are retained; equal string contents alone do not authorize interning.
  Reflective calls require proven normal continuation and no side effects before lowering.

## Kotlin demo and debugger consumers

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


