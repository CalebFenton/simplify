# Android emulator

A real Android emulator in Docker — API 36, headless, KVM-accelerated. Run dex on it directly with `dalvikvm`; no APK and no app packaging needed.

Use it for anything the lightweight `tools/art-runner/` can't reach: `android.*` classes, current-ART behaviour, and the `nterp` interpreter tier. For plain opcode and `java.*` semantics prefer art-runner — it answers in milliseconds instead of needing a booted device.

## Use

```
tools/emulator/build-image.sh    # once, ~85s
tools/emulator/start.sh          # ~70s cold boot; safe to re-run
tools/emulator/run-dex.sh        # runs the art-runner fixtures here
tools/emulator/shell.sh          # shell inside the container
tools/emulator/stop.sh
```

Start it once and leave it running — then `run-dex.sh` and `shell.sh` are instant. Cold boot is expected and fine.

The host needs Docker, read/write access to `/dev/kvm`, and the repository's
Gradle-compatible JDK. The image provides the Android SDK, emulator and `adb`; those
commands need not be on the host's `PATH`. Fixture assembly uses the shared
`tools/art-runner/assemble.sh` workflow with Google's `com.android.tools.smali:smali:3.0.10`.
It resolves the assembler classpath from Gradle each time rather than reusing the old
`build/art-runner/smali-classpath.txt` cache.

Ad-hoc:

```
tools/emulator/run-dex.sh path/to/foo.dex SomeClass arg1 arg2
```

With no arguments it runs the fixtures from `tools/art-runner/fixtures/`, so you get a direct comparison against art-runner for free.

## Cross-validation: this tool is Tier B

`tools/art-runner/` is Tier A — a bare Android-10/11-vintage `art_standalone`. This is Tier B — a real API-36 device. `cross-validate.sh` runs the whole fixture corpus here in both interpreter modes and diffs it against the Tier A golden, so a real device periodically re-checks every settled question.

```
tools/emulator/cross-validate.sh            # measure + print the agreement numbers
tools/emulator/cross-validate.sh --update   # re-record golden/tier-b-batch.txt
```

Last recorded: the `nterp` default and `-Xint` agree 77 of 77 on this device, and Tier A `-Xint` vs Tier B default agree on type+value for **75 of 77** cells — **zero opcode/`java.*` semantic divergences across the ART-version gap**. The two exceptions are the `android.*` boundary probes below (a classpath difference, not a semantics one); everything else that differs is ART wording drift, which is compared away by type. `golden/tier-b-batch.txt` is the pinned capture and diffs directly against `tools/art-runner/golden/tier-a-batch.txt`.

It also re-derives art-runner's `-Xint` mandate on real hardware: `Fx_AputObjectIncompatible` throws `ArrayStoreException` at the device default, matching Tier A `-Xint` and contradicting Tier A's AOT, which silently drops it. So Tier A's AOT is the outlier and `-Xint` is the oracle.

For a fresh comparison after an assembler upgrade, leave both goldens unchanged:

```sh
tools/art-runner/assemble.sh --force
tools/art-runner/check-golden.sh
tools/emulator/stop.sh && tools/emulator/start.sh
tools/emulator/cross-validate.sh
```

The Tier A comparison requires its local image label to match the existing Dockerfile
pin; see `tools/art-runner/README.md`. The Tier B comparison forces another fresh assembly
before its two runs. Check that each run reached `DISPATCH_END` and that
`build/emulator/last-run.stderr` is empty. Read any drift before requesting approval to
use `--update`.

## Reaching the framework: when `dalvikvm` is enough

Bare `dalvikvm` reaches more of `android.*` than you'd expect. The line is **JNI**, not "the framework":

- **Pure-Java framework classes just work.** `android.util.Base64.encodeToString` returns `aGk=` for `"hi"` — no app process, no APK.
- **JNI-backed ones can't be reached at all.** `android.os.Build.MODEL` throws `UnsatisfiedLinkError`: `SystemProperties.native_get` has no implementation registered.

**And you can't fix that by loading the library yourself.** `System.loadLibrary("android_runtime")` fails with `dlopen failed: library "libandroid_runtime.so" not found` — a shell process doesn't get the linker namespace an app process gets, so the library isn't even visible. There is no workaround from here.

So the rule, in the order you'll actually apply it:

1. **Just run it on `dalvikvm`.** The failure is loud and unambiguous, so this costs nothing.
2. **`UnsatisfiedLinkError` means it's JNI-backed** — that question needs a real app process, and this tool can't answer it. Nothing about the harness will change that.
3. **Anything else** — a value, a throw, a `VerifyError` — is a real answer you can trust.

`Context`, app lifecycle, and manifest-declared entry points are all in category 2. Running those needs an app process, which isn't built here.

Three committed fixtures pin all of this: `Fx_AndroidBase64`, `Fx_AndroidBuildModel`, `Fx_AndroidBuildModelAfterLoadLibrary`. Run them on both tools and the whole boundary is visible in one command — on art-runner the first two fail with `NoClassDefFoundError` (it has no `android.*` at all) and the third with `UnsatisfiedLinkError` at the `loadLibrary` call itself, before any framework class is touched.

To run just these framework boundary checks after the cold boot and fresh assembly above:

```sh
ART_RUNNER_MODE=int tools/art-runner/run-batch.sh \
  Fx_AndroidBase64 Fx_AndroidBuildModel Fx_AndroidBuildModelAfterLoadLibrary
EMULATOR_ART_FLAGS= tools/emulator/run-dex.sh \
  build/art-runner/art-dispatcher.dex:build/art-runner/art-fixtures.dex RunFixtures \
  Fx_AndroidBase64 Fx_AndroidBuildModel Fx_AndroidBuildModelAfterLoadLibrary
```

For this explicit dex/class invocation, confirm `DISPATCH_END` in the output yourself
and check that `build/emulator/last-run.stderr` is empty. Repeat the Tier B command with
`EMULATOR_ART_FLAGS=-Xint` to isolate interpreter-mode differences.

## Worth knowing

**Don't pin ART message text.** Diagnostic strings drift between versions: API 36 words a `VerifyError` slightly differently from art-runner's older ART. Assert on the throwable class, the value, and the bytecode offset.

**stderr means something here.** A clean run leaves it empty; ART's chatter goes to logcat. That's the opposite of art-runner.

**`stop.sh --keep` then `start.sh` restores in ~6s** via the emulator's quickboot snapshot. Plain `stop.sh` removes the container so the next start is genuinely cold — worth preferring when a result matters, so nothing inherits a stale device.

**Never push this image anywhere.** Android system images can't be redistributed; build it locally and keep it local.

If the build ever breaks, `us-docker.pkg.dev/android-emulator-268719/images/30-google-x64-no-metrics` is a prebuilt fallback that boots in about a minute, capped at API 30.
