# ART runner

Runs Dalvik bytecode on real ART and tells you what actually happened. It's real AOSP ART (`libart.so`, `dalvikvm`) built as a native Linux binary — no emulator, no KVM, no Android boot. 373 MB, builds in ~12s, ~0.6ms per fixture.

Use it for opcode and `java.*` semantics: what an opcode stores, what it throws, what the verifier rejects. It has no `android.*` at all, so for framework questions use `tools/emulator/` — which reaches more of `android.*` than you'd expect, since bare `dalvikvm` handles pure-Java framework classes fine and only JNI-backed ones are out of reach. See that tool's README for the rule.

## Use

```
tools/art-runner/build-image.sh                  # once
tools/art-runner/run-batch.sh                    # all fixtures
tools/art-runner/run-batch.sh Fx_DivOverflow     # one
```

Assembly uses SmaliVM's Smali dependency, resolved by `:smalivm:printSmaliAssemblerClasspath`,
and its `com.android.tools.smali.smali.Main` CLI.
The host needs the repository's Gradle-compatible JDK; Docker supplies ART, so a host
Android SDK is not required. Both the fixture assembler and the isolated Tier B probe
runner resolve their classpath through Gradle on every invocation.
The API level comes from `SmaliParser.DEX_API_LEVEL`, with `--jobs 1` for assembly.

One line per fixture:

```
FIXTURE	Fx_DivOverflow	OK	java.lang.Integer(-2147483648)
FIXTURE	Fx_AputObjectIncompatible	THREW	java.lang.ArrayStoreException	java.lang.Object cannot be stored in an array of type java.lang.String[]
```

## Adding a question

Drop a `.smali` file in `fixtures/` with a `public static Object run()` returning something observable, then re-run. To observe a throw, let it propagate — the dispatcher catches and reports it.

Two naming rules:

- **`Fx_*`** is a fixture and gets invoked automatically.
- **`Probe_*`** is a support class — something a fixture acts *on*, like a class whose `<clinit>` prints when it initializes. Never auto-invoked, because reaching a class reflectively initializes it, which would corrupt any fixture measuring whether something else initialized it.

## The golden

`golden/tier-a-batch.txt` is what Tier A answered when it was recorded. `check-golden.sh` re-runs the batch and fails if the answers moved, because bumping the pinned ART image is otherwise a one-line edit that silently re-answers settled questions.

```
tools/art-runner/check-golden.sh             # compare
tools/art-runner/check-golden.sh --update    # accept a change, once you know what changed
```

Run `--update` only after reading the diff and deciding which of three things happened: the image changed (the script prints the versions first, so you can rule that out without thinking), a fixture changed, or ART changed its answer. Only the last is a finding. What the comparison normalizes away, and why each rule is there, is in the script's header.

The header also records the dex API level and a SHA-256 of every dispatcher and fixture source. `DeviceFixturesTest` recomputes both in `./gradlew test`, so CI fails when a fixture changes without a new recording. CI does not run `check-golden.sh` itself: it needs the pinned `art_standalone`, which Alpine `edge/testing` no longer publishes.

After changing assembler dependencies, compare freshly assembled dexes without changing
the recorded data:

```sh
tools/art-runner/assemble.sh --force
tools/art-runner/check-golden.sh
```

An assembly-script change also invalidates both dex outputs. Before interpreting any
comparison, check that the local image's
`org.cf.simplify.art-standalone-version` label matches `ARG ART_VER` in the Dockerfile:

```sh
docker image inspect simplify-art-runner:pinned \
  --format '{{index .Config.Labels "org.cf.simplify.art-standalone-version"}}'
```

If the label differs, restore the matching pinned image from a saved copy. `build-image.sh` can no longer
rebuild this pin, because Alpine `edge/testing` no longer publishes it; moving to a new pin means changing
`ARG ART_VER`, reading the full `check-golden.sh` diff, and re-recording. Do not change the Dockerfile or
accept a new golden merely to accommodate a different image already on the host.

## Worth knowing

**It always runs `-Xint`.** This ART's AOT compiler is unsound — it silently drops an `ArrayStoreException` the interpreter throws correctly. `verify-xint-mandate.sh` re-proves that on demand.

**It is not a current ART.** Android 10/11 vintage with no `nterp`, so it can't answer anything about which interpreter tier reads a register how. That needs `tools/emulator/`.

**No `android.*`.** Framework classes fail with `NoClassDefFoundError` — but code that calls `System.loadLibrary` first fails *earlier*, with `UnsatisfiedLinkError`, because there is no `libandroid_runtime.so` either; it never reaches a framework class at all. `Fx_AndroidBase64` and `Fx_AndroidBuildModel` pin the first case, `Fx_AndroidBuildModelAfterLoadLibrary` the second.

**Ignore the exit code.** The VM aborts in a shutdown thread after your program finishes. stdout is fine; the scripts gate on a `DISPATCH_END` sentinel.

**Don't pin ART message text** — diagnostic strings change between Android versions. Assert on the throwable class, the value, and the bytecode offset. This rule is about fidelity assertions across runtimes and deliberately does *not* apply to the golden, which pins message text so that a change is noticed; `check-golden.sh` explains why.

## Citing a measurement — the `[DEVICE]` tag

A measurement from this runner is cited `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` in `docs/DALVIK-SEMANTICS.md`. The parenthetical — **tier, interpreter mode, artifact version** — is mandatory, because the two facts that most nearly broke this harness are invisible without it: the AOT tier is unsound (hence `-Xint`), and a modern device's default interpreter (`nterp`, Tier B only) reads a register differently. A citation that says only "measured on a device" hides both, so one missing field means it is not a `[DEVICE]` citation.

`[DEVICE]` is **tier-1**: on a question of what the runtime does it outranks an `[ART]` source reading, Android's docs, [JLS]/[JVMS], and the core's own expectations. Two things it can't settle — a verifier question about input ART refuses to link (Tier A *can* report ART's own verdict as a catchable `VerifyError`, which is its alone), and a register-read question (that needs Tier B's `nterp`). The rule, the exceptions and the ranking live in `docs/DALVIK-SEMANTICS.md`'s tier ranking.

Every citation must name a fixture here so anyone can re-run it in under a second (`run-batch.sh Fx_Name`). A measurement nobody can reproduce is an anecdote with a better tag.

## Cross-checking

`tools/emulator/run-dex.sh` runs these same fixtures on a real API 36 image. Agreement across both is strong evidence; disagreement is a finding about ART versions or tiers, not something to smooth over.
