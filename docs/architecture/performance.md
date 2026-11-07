# Performance Contracts

## Resolver cost

The handler resolver uses cached immutable `ThrowableHierarchy` facts and persistent required/excluded root sets.
It scans each clause once. Persistent-set updates add their normal tree cost, so the claim is near-linear rather
than strict linear time. No handler cache and no path-width cap exist.

Each domain update creates one wrapper and path-copies only the changed persistent-set path. Validation visits the
inserted root, not all prior roots. `HandlerResolutionTest` resolves 100,000 missing-type clauses within a fixed
time bound that a per-clause walk of the prior roots cannot meet.

## Completion and routing cost

The scheduler partitions caller throws before handler resolution. For `E` exits and `P` source keys, it performs `P`
state merges and `P` resolver calls. It expands resolved routes once per original exit. Output fanout therefore
remains visible, while equivalent exits do not repeat merge and lookup work.

`ThrowableDomain` updates required-root and excluded-root hashes incrementally and caches the complete domain hash,
so source-key hashing does not traverse domain roots. Hash-map equality can still compare persistent sets after
matching hashes.

`CallCompletion` hands its exit list to one pending batch when it drains. Routing releases that batch in `finally`.
Limit and timeout aborts release pending batches and open collectors and record one aborted outcome per retained
exit.

## Persistent copy cost

Frame, heap, and class-init copies share persistent roots and visit no state entries. `PersistentStateTest` copies a
frame holding 100,000 heap objects and 10,000 statics 100,000 times within a fixed time bound. Public-entry
sanitization intentionally scans register slots.

Compatibility keys cache the `Map`-contract hash, so grouping call completions never walks the class-init or
tracked-field maps; keys over the same persistent root compare in constant time.

Op-arm reconciliation joins only the register, array, object-field, and static-field cells that the arms' updates
name. `OpArmStateTest` reconciles 2,000 forks that each write one cell of a 2^20-element array within a fixed time
bound.
