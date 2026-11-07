# Performance Contracts

## Deterministic resolver cost

The handler resolver uses cached immutable `ThrowableHierarchy` facts and persistent required/excluded root sets.
It scans each clause once. Persistent-set updates add their normal tree cost, so the measured structural claim is
near-linear rather than strict linear time. No handler cache and no path-width cap exist.

The adversarial `H=2048` test records: 2048 clauses visited, 2048 routes, 2049 fact builds, 2048 missing
descriptors, 4096 relation requests, 4096 relation cache misses, 4096 `domainWrappers`, 4096
`persistentUpdates`, and 4096 `domainValidationVisits`. The maximum roots are 2048 excluded and one required.
Persistent sets retain prior roots structurally. Each update creates one wrapper and path-copies only the changed
persistent-set path. Validation visits the inserted root, not all prior roots. These are deterministic structural
counters, not elapsed-time or allocation measurements.

## Completion and routing cost

The scheduler partitions caller throws before handler resolution. For `E` exits and `P` source keys, it performs `P`
state merges and `P` resolver calls. It expands resolved routes once per original exit. Output fanout therefore
remains visible, while equivalent exits do not repeat merge and lookup work.

`ThrowableDomain` updates required-root and excluded-root hashes incrementally. It caches the complete domain
hash. Source-key hash reads do not traverse domain roots. Hash-map equality can still compare persistent sets
after matching hashes.

The eight-exit, sixteen-clause structural test asserts eight cached hash reads, zero root visits, one source
merge, and one resolver call. These counters are deterministic. They are not elapsed-time or allocation data.

`CallCompletion` transfers exits to one pending batch when it drains. Routing releases that batch in `finally`.
Limit and timeout aborts release pending batches and open collectors. After routing or abort, completion metrics
report zero retained frames and zero retained collection capacity. This is an ownership counter, not a heap
profile.

## Persistent copy cost

Every frame-copy boundary declares its state-entry visit count. Ordinary and op-arm copies report zero visits and
retain identical register, heap, class, static, host-graph, read, and address-visit roots. Public-entry
sanitization supplies the nonzero calibration because it intentionally scans register slots. Reconciliation
counters separately report only named register, array, object-field, and static-field cells.
