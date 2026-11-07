# SmaliVM Architecture

SmaliVM models Dalvik outcomes with unknown inputs and explicit exploration limits. Simplify consumes
complete execution evidence to rewrite immutable program revisions; SDBG drives the same execution
session rather than a separate interpreter.

## Contracts

| Document | Scope |
|---|---|
| [Execution engine](execution-engine.md) | Call completion, scheduler routing, cancellation, and ownership |
| [State model](state-model.md) | Persistent state, throwable refinements, lifecycle state, and host identity |
| [Exceptions](exceptions.md) | Ordered handlers, cross-frame routing, and initialization failures |
| [Graph and optimizer](graph-optimizer-contract.md) | Immutable revisions, editing, evidence gates, and consumer APIs |
| [Performance](performance.md) | Deterministic routing and state-copy cost contracts |
| [Logging ownership](startup-and-policy.md#packaged-logging-ownership) | Library and application logging boundaries |
| [Build and distribution](build-and-distribution.md) | Modules, JVM ABI, artifacts, toolchain, and publication |
| [Dalvik semantics](../DALVIK-SEMANTICS.md) | Runtime sources, device evidence, and intentional semantic divergences |
