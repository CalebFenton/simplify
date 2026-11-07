# ART Runner Boundaries

- Route `android.*`, API 36, and `nterp` adjudication to Tier B.
- Cite Tier A mode, artifact version, exact `Fx_*` fixture, value, throwable class, and bytecode offset.
- Require `DISPATCH_END`; the post-main process exit status is not semantic evidence.
- Treat framework failures as Tier A classpath limits.
- Read the version block and diff, then request approval before `tools/art-runner/check-golden.sh --update`.
- Never use AOT as an oracle. Run it only for the known divergence with:
  `ART_RUNNER_MODE=aot ART_RUNNER_ALLOW_UNSOUND_AOT=1 tools/art-runner/run-batch.sh Fx_Name`.
- Name auto-run fixtures `Fx_*` and support witnesses `Probe_*`; reflection initializes named classes.
