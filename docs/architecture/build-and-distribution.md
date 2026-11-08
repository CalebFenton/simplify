# Build and Distribution Architecture

This document describes the Gradle build, the embedding boundary, artifact contents, and CI.

## Toolchain and dependencies

The Gradle wrapper (`gradle/wrapper/gradle-wrapper.properties`) pins the Gradle version and its distribution
SHA-256. Every module compiles with a JDK 21 toolchain to JVM 21 bytecode, so building needs a local JDK 21:
the build does not download one. Running the applications requires JDK 21 or later.

`gradle/libs.versions.toml` holds every dependency and plugin version. Dependencies resolve from Google Maven
and Maven Central only. No dependency lockfile, checksum manifest, publication repository, or signing
workflow is configured. The root `build.gradle` applies Kotlin/JVM and JaCoCo and the shared compile, test,
JAR, and application settings; each module declares only its role and dependencies. Kotlin sources live under
`src/*/java`.

`gradle.properties` raises the Gradle daemon heap, because ProGuard's whole-library analysis of the SmaliVM
fat JAR needs more than the default heap.

## Modules and public library boundary

| Module | Role |
|---|---|
| `smalivm` | Library: execution engine, immutable snapshots, method editing, DEX and class infrastructure |
| `simplify` | Optimizer and command-line application |
| `demoapp` | Embedding example |
| `sdbg` | Interactive debugger; renders disassembly with Baksmali |

SmaliVM exports Google's maintained [Smali](https://github.com/google/smali) artifacts through its `api`
configuration, because its public signatures use `com.android.tools.smali` DEX types. Embedding callers get
the same Smali version SmaliVM parses with.

Android Base64 is compiled from pinned AOSP `android-7.1.1_r1` source, with its original Apache header and
provenance in `THIRD_PARTY_NOTICES`.

`simplify/ObfuscatedApp` is a standalone Android project, not a root subproject. Its Android Gradle Plugin,
Gradle, and SDK versions are independent of the host build. The checked-in `simplify/obfuscated-app.apk` is a
test input; the root build does not rebuild, sign, or install it.

## Host reflection module opening

SmaliVM reflects into `java.lang` objects when it runs host methods, so the JVM must run with
`--add-opens=java.base/java.lang=ALL-UNNAMED`. The root `build.gradle` defines the opening once and applies it
to tests, application fat-JAR manifests (`Add-Opens`), and distribution launch scripts. Applications that embed
SmaliVM must pass the flag themselves.

## Logging ownership

SmaliVM depends only on the SLF4J API. It packages no logging provider and no root `logback.xml`, so an
embedding application chooses its own provider and configuration. Simplify and SDBG each ship a `logback.xml`
with a WARN root level and use Logback APIs for their verbosity controls. The demo uses `slf4j-nop`. SmaliVM's
`logback-test.xml` configures its own tests only.

## Artifacts and tasks

| Task | Output |
|---|---|
| `build` | Thin JARs, tests, and application TAR/ZIP distributions for all modules |
| `:simplify:fatJar` | `simplify/build/libs/simplify.jar` |
| `:demoapp:fatJar` | `demoapp/build/libs/demoapp.jar` |
| `:sdbg:fatJar` | `sdbg/build/libs/sdbg.jar` |
| `:smalivm:fatJar` | `smalivm/build/libs/smalivm-<version>-all.jar`, with runtime dependencies |
| `:smalivm:proguard` | `smalivm/build/libs/smalivm-<version>-proguard.jar` |
| `jacocoRootReport` | Aggregate HTML/XML coverage for `smalivm` and `simplify` |
| `:smalivm:printSmaliAssemblerClasspath` | Runtime classpath for `tools/art-runner` |

Fat JARs are explicit tasks, not dependencies of `build`, because each one copies the full framework corpus.
They unpack the runtime classpath, drop duplicate entries, and in applications drop dependency signature files.
Every JAR carries `META-INF/LICENSE`, `META-INF/THIRD_PARTY_NOTICES`, and per-component `META-INF/licenses/`
texts; application ZIP/TAR distributions also carry them at the top level. JAR tasks enable ZIP64 because the
API 36 framework plus runtime dependencies exceeds the ZIP entry limit. Fat-JAR packaging does not merge
service descriptors.

The legal bundle under `licenses/` matches the declared dependency versions. A dependency update must update
the matching component texts, copyright notices, and source locations.

`:smalivm:proguard` builds an optimized SmaliVM JAR that `tools/ci/smoke.py` runs Simplify, the demo, and SDBG
against in place of the ordinary SmaliVM classes. `runProguard` optimizes the SmaliVM fat JAR's JVM code with
JMODs from the build JVM, keeps public APIs and attributes, and does not obfuscate. ProGuard's ZIP writer
cannot hold the framework's entry count, so `runProguard` excludes the framework Smali and `proguard`
repackages its output with that Smali using Gradle's ZIP64 writer.

## Framework and Android execution inputs

The framework submodule supplies the Android 16 / API 36 corpus from `android-36/smali/`. The checkout needs a
case-sensitive filesystem, because the corpus has class files whose names differ only by case.
`generateFrameworkCatalog` writes the catalog resource during `processResources`, with source hashes and method
declarations. The catalog records Android API 36 separately from DEX version 039; framework parsing derives
its opcode table from the DEX version, independent of the application's input and output API levels.
`processResources` packages only `android-36/smali/**/*.smali` from the submodule. The legal bundle includes
the API 36 provenance, boot and source manifests, and original notices.

`ReflectionPolicy` decides which host calls run; framework availability does not grant permission to run host
code. The `*.cfg` files under `smalivm/src/main/resources` list the classes and methods the default policy
permits or denies.

The ART assembler in `tools/art-runner` asks Gradle for SmaliVM's runtime classpath on each run, so fixtures
assemble with the same Smali version and explicit DEX API level as SmaliVM. Device comparison runs outside the
JVM tests; see `tools/art-runner/` and `tools/emulator/`.

Simplify reads Smali, DEX, and APK inputs in memory with dexlib2 and Smali. It rejects an APK that contains
`classes2.dex`. APK output replaces `classes.dex`, preserves the bytes, compression, timestamps, and comments of
every other entry and the archive comment, and must be signed again before installation.

## CI

`.github/workflows/build.yml` builds and tests pull requests and pushes to `master` and `release/**` with JDK 21
and the framework submodule. It builds the application fat JARs and the ProGuard JAR with `--continue`, so
independent test suites finish after another task fails; any failure still fails the job. Runs for a pull
request cancel the previous run for that pull request.

`tools/ci/test_report.py` writes per-module test counts to the console and the job summary. After a successful
build it requires test results to exist. The workflow uploads JUnit and JaCoCo reports even after a failure and
requires them after a successful build. Coverage has no minimum-percentage gate.

`tools/ci/smoke.py` launches the fat JARs and the extracted ZIP distributions outside Gradle's test classpath.
It checks Smali, DEX, and APK processing, the demo, and debugger stepping, and repeats Simplify, demo, and
debugger runs against the ProGuard JAR. Both scripts need Python 3.9 or later and its standard library. To run
them locally after building the artifacts:

```sh
python3 tools/ci/test_report.py --require-results
python3 tools/ci/smoke.py
```

`smoke.py` uses `JAVA_HOME` when set and requires exactly one ZIP distribution per application. `TMPDIR` selects
where it extracts distributions. Neither script rebuilds artifacts. Android comparisons in the JUnit suite use
committed ART recordings; CI does not boot an emulator.

## Licensing and release

Project-owned code is Apache-2.0. Dependencies, vendored source, and the Android framework corpus keep their
upstream terms; `THIRD_PARTY_NOTICES` records the license boundary and the API 36 capture's image fingerprint
and tool hashes. All modules share the version in the root `build.gradle`. A release removes `-SNAPSHOT` when
tagging; build tasks do not sign APKs, publish packages, or create tags.
