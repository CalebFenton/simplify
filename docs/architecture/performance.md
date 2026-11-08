# Performance Contracts

Each contract below names the test that enforces it. A cost test runs the same work on a small and a large input
and bounds the ratio of their CPU times (`assertCostRatio`). The bound sits well above the ratio the contract's
algorithm reaches and well below the ratio of the slower algorithm it rules out, so the test depends on how cost
grows, not on how fast or how loaded the host is.

## Handler resolution is near-linear in the clause count

The handler resolver uses cached immutable `ThrowableHierarchy` facts and persistent required and excluded root
sets, scanning each catch clause once. Persistent-set updates add their tree cost, so resolution is near-linear
in the clause count. `HandlerResolutionTest` resolves tables of missing catch types eight times apart in size;
a resolver that re-walks the excluded roots per clause grows quadratically and exceeds the bound.

## Persistent state copies do not visit state entries

`Frame`, heap, and class-init copies share persistent roots. `PersistentStateTest` copies a frame holding many
heap objects and statics at the cost of copying a nearly empty one, and runs a long no-write execution over a
large seeded heap at the cost of an empty one. Public-entry sanitization scans register slots.

## Compatibility keys hash and compare without walking their maps

`CallCompatibilityKey` caches its `Map`-contract hash, and keys over the same persistent root compare equal
without walking entries. `CompatibilityMapKeyTest` counts element `hashCode` and `equals` calls and requires
none on repeated hashing, shared-root equality, or unequal hashes. `PersistentStateTest` groups by keys over
large class-init and tracked-field maps at the cost of grouping by keys over one-entry maps.

## Op-arm reconciliation follows the named cells

Reconciling an op's arms joins only the register, array, object-field, and static-field cells the arms' updates
name. `OpArmStateTest` reconciles forks that each write one cell of a large array in a large frame at the cost
of the same forks in a small frame.

## Class-init planning walks the superclass chain once

Each class-init plan walks the requested class's superclass chain once, so a chain of initializers plans in
time linear in its length per plan. `ClassInitHostileInputTest` plans chains eight times apart in length; a walk
that rescans the chain for each class it adds grows quadratically per plan and exceeds the bound.

## Routing releases completion storage

`CallCompletion` hands its exit list to one pending batch when it drains, and routing releases that batch.
Limit and timeout aborts release pending batches and open collectors in one pass and record one aborted outcome
per retained exit. `CallCompletionTest` and `CallRoutingReliabilityTest` check that released batches retain no
frames, and `CallRoutingReliabilityTest` aborts runs with open activations eight times apart in number.

## Dead-assignment liveness is quadratic in the worst case

Liveness keeps sorted register sets per instruction and revisits an instruction only when a successor's set
changes. A loop that keeps every register live is quadratic in method size. `DeadRemovalStrategyTest` analyses
such loops eight times apart in size; a worklist that moves liveness back one instruction per pass is cubic and
exceeds the bound.
