# Execution Engine

## Call completion and routing

The execution engine seals call completion behind `CallCompletion`. `ActivationExit.Returned`, `Threw`, and `Aborted`
carry terminal frame state. `replacePath()` updates the private path count and exit list together.
`drainIfComplete()` returns null while paths remain open. At zero, it returns one `CompletionBatch` and releases
the collector. A second drain or late vote is an invariant failure.

The session queues each batch in `pendingCallRoutings`. This queue is scheduler work, not guest execution.
`step()` drains it at scheduler boundaries. Routing creates no `ExecutionNode`, frontier item, breakpoint, or
debugger cursor. `isFinished` requires an empty frontier and an empty routing queue. Routing consumes step and
time limits, but it records no address visit.

Each routing command crosses one activation boundary. It searches the immediate caller at the invoke address.
A residual escape enters the parent `CallCompletion`. It waits until that activation drains. The host stack does
not recursively traverse callers.

Caller throws are grouped by source key before handler resolution; [Exceptions](exceptions.md) defines the
key. One source key causes one state merge and one resolver call, and routing repeats the resolved result for
each original exit.

An ordinary nested-callee abort can become a `STRONG` opaque caller continuation. It does not add a run-level
abort reason. Top-level aborts, class-initializer aborts, and cancellation remain `Partial` and untrusted.

Routing releases its batch in a `finally` block. Limit and timeout cancellation release all pending batches and
all open collectors, so released completion storage retains no frames.
