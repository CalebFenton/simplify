# SmaliVM Architecture

SmaliVM models Dalvik outcomes with unknown inputs and explicit exploration limits. Simplify consumes
complete execution evidence to rewrite immutable program revisions; SDBG drives the same execution
session.

## Contracts

| Document | Scope |
|---|---|
| [Execution engine](execution-engine.md) | Call completion, scheduler routing, cancellation, and ownership |
| [State model](state-model.md) | Persistent state, throwable refinements, lifecycle state, and host identity |
| [Exceptions](exceptions.md) | Ordered handlers, cross-frame routing, and initialization failures |
| [Graph and optimizer](graph-optimizer-contract.md) | Immutable revisions, editing, evidence gates, and consumer APIs |
| [Performance](performance.md) | Deterministic routing and state-copy cost contracts |
| [Build and distribution](build-and-distribution.md) | Modules, toolchain, artifacts, logging, embedding, and CI |
| [Dalvik semantics](../DALVIK-SEMANTICS.md) | Runtime sources, device evidence, and intentional semantic divergences |
