# Performance Contracts

Each contract below names the test that enforces it. The tests bound wall-clock time with
`assertTimeoutPreemptively`, sized so that the slower algorithm the contract rules out cannot finish.

## Handler resolution is near-linear in the clause count

The handler resolver uses cached immutable `ThrowableHierarchy` facts and persistent required and excluded root
sets, scanning each catch clause once. Persistent-set updates add their tree cost, so resolution is near-linear
in the clause count. `HandlerResolutionTest` resolves a large table of missing catch types within its bound;
a resolver that re-walks the excluded roots per clause cannot.

## Persistent state copies do not visit state entries

`Frame`, heap, and class-init copies share persistent roots. `PersistentStateTest` copies a frame holding many
heap objects and statics repeatedly within its bound, and runs a long no-write execution without copying live
state per node. Public-entry sanitization scans register slots.

## Compatibility keys hash and compare without walking their maps

`CallCompatibilityKey` caches its `Map`-contract hash, and keys over the same persistent root compare equal
without walking entries. `CompatibilityMapKeyTest` counts element `hashCode` and `equals` calls and requires
none on repeated hashing, shared-root equality, or unequal hashes. `PersistentStateTest` groups completions by
keys over large class-init and tracked-field maps within its bound.

## Op-arm reconciliation follows the named cells

Reconciling an op's arms joins only the register, array, object-field, and static-field cells the arms' updates
name. `OpArmStateTest` reconciles many forks that each write one cell of a large array within its bound.

## Routing releases completion storage

`CallCompletion` hands its exit list to one pending batch when it drains, and routing releases that batch.
Limit and timeout aborts release pending batches and open collectors and record one aborted outcome per
retained exit. `CallCompletionTest` and `CallRoutingReliabilityTest` check that released batches retain no frames.
