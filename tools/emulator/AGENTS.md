# Emulator Boundaries

- Route `android.*`, API 36, and `nterp` adjudication to this Tier B tool. Use Tier A first otherwise.
- Cite mode, API, artifact provenance, exact `Fx_*` fixture, value, and throwable.
- Treat `UnsatisfiedLinkError` as the JNI boundary, not framework evidence.
- For consequential results, cold boot with `tools/emulator/stop.sh && tools/emulator/start.sh`.
- Require `DISPATCH_END` and empty `build/emulator/last-run.stderr`.
- Read the diff and request approval before `tools/emulator/cross-validate.sh --update`.
- Never distribute the emulator image.
