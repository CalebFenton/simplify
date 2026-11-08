# Execution Engine

## Call completion and routing

Every activation reports its ended paths to a `PathSink`: the entry activation's sink is `RunOutcomes`, and a
callee's is `CallCompletion`. `ActivationExit.Returned`, `Threw`, and `Aborted` carry terminal frame state.
`CallCompletion.replacePath(openPaths, ended)` updates the open-path count and exit list together and returns
one `CompletionBatch` when the count reaches zero. A vote after release is an invariant failure.

The session queues each batch in `pendingCallRoutings`. This queue is scheduler work, not guest execution.
`step()` drains it at scheduler boundaries. Routing creates no `ExecutionNode`, frontier item, breakpoint, or
debugger cursor. `isFinished` requires an empty frontier and an empty routing queue. Routing consumes step and
time limits, but it records no address visit.

Each routing command crosses one activation boundary. It searches the immediate caller at the invoke address.
A residual escape enters the parent `CallCompletion`. It waits until that activation's paths all end. The host stack does
not recursively traverse callers.

Caller throws are grouped by source key before handler resolution; [Exceptions](exceptions.md) defines the
key. One source key causes one state merge and one resolver call, and routing repeats the resolved result for
each original exit.

An ordinary nested-callee abort can become a `STRONG` opaque caller continuation. It does not add a run-level
abort reason. Top-level aborts, class-initializer aborts, and cancellation remain `Partial` and untrusted.

Routing releases its batch in a `finally` block. Limit and timeout cancellation release all pending batches and
all open collectors, so released completion storage retains no frames.

## Memory limit

A run's memory grows with its step count. Every `ExecutionNode` stays reachable from the entry node through its
control-flow links until the run's graph is dropped, including the nodes of callees that already returned. Most
nodes can belong to one callee activation that forks heavily and never drains before a limit ends the run, so
releasing returned callees would not bound a run.

`TenuredHeapPressure` stops that growth before it exhausts the heap. On first use it sets a collection-usage
threshold of 80% of the maximum on every tenured heap pool: the heap pools that support a usage threshold,
which excludes eden and survivor spaces. The JVM then counts each collection that collected that pool and left
it at or above the threshold: a G1 mixed, full, or concurrent-cycle collection, or a Parallel or Serial full
collection. Young-only collections never count.

`ExecutionSession` reads that count when the run starts and at every scheduler step. When it has grown, the
session runs one whole-heap collection (`System.gc()`) and reads the tenured pools again, because a reading
after a mixed or concurrent-cycle collection still includes garbage the collector has not evacuated, such as
the graph of the previous method's run. If live data is still at or above the threshold, the whole run ends as
`MEMORY_LIMIT` before the next op runs, the same way `METHOD_LIMIT` and `TIMEOUT` end it: every scheduled node
and open callee exit becomes an aborted path, completion is `Partial`, and the graph becomes garbage once the
optimizer drops it. Otherwise the run continues, and only a later report triggers another confirmation. A
report from before the run started never stops it, so the method after a skipped one runs normally. A JVM
that ignores explicit collections (`-XX:+DisableExplicitGC`) keeps the reading that reported pressure, so the
run stops.

80% leaves room for G1's evacuation reserve (`G1ReservePercent`, 10%), its minimum young generation
(`G1NewSizePercent`, 5%), and what a run allocates between the collection that reports pressure and its next
step. With live data above about 90%, G1 falls back to back-to-back full collections and then throws
`OutOfMemoryError`.

The scheduler never catches `OutOfMemoryError`: `hostFailureOutcome` rethrows every `VirtualMachineError`, so
a heap exhausted before a report still ends the process. The limit depends on the heap size and on what else
the process retains, so, like `TIMEOUT`, it is not reproducible from the input alone.
