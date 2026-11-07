#!/usr/bin/env bash
# Runs one batch of art-runner fixtures on real ART and prints the observations.
#
#     tools/art-runner/run-batch.sh                          # every fixture
#     tools/art-runner/run-batch.sh Fx_DivOverflow Fx_...    # a named subset
#
# Environment:
#     ART_RUNNER_IMAGE                 image tag (default simplify-art-runner:pinned)
#     ART_RUNNER_MODE              int | aot   (default int; see THE -Xint MANDATE)
#     ART_RUNNER_ALLOW_UNSOUND_AOT=1   required to select `aot`, which is not an oracle
#
# Exit status: 0 iff the sentinel arrived. Fixtures that throw are DATA, not failures --
# a thrown exception is frequently the answer the harness was built to get.
#
# THIS SCRIPT ENFORCES FOUR INVARIANTS, all of them measured rather than theorised.
#
# 1. THE -Xint MANDATE. This image's AOT compiler is unsound. Measured 5/5: with default
#    flags, `aput-object` storing a `java.lang.Object` into a genuine `[Ljava.lang.String;`
#    SUCCEEDS and reads back as `java.lang.Object`; a real device (API 30) throws
#    `ArrayStoreException` in both modes. `dex2oat` here is Android 10/11-vintage. `-Xint`
#    matched device ART byte-identically on all 7 spike fixtures, so the interpreter is the
#    oracle and the compiler is not. `verify-xint-mandate.sh` re-proves this on demand.
#
# 2. THE DEX PATH MUST BE >= 17 CHARACTERS. Upstream `size_t` underflow in
#    `art/runtime/oat_file_assistant.cc`:
#        dex_location_.compare(dex_location_.size() - 17, 17, "framework-res.apk")
#    Measured: 11-15 character paths abort with `std::out_of_range`; 17+ work. Asserted per
#    classpath entry below, because the failure mode is a C++ abort with no useful message
#    and it took a bisection to find the first time.
#
# 3. GATE ON THE SENTINEL, NEVER ON THE EXIT STATUS. ART's shutdown thread crashes
#    (SIGABRT/SIGSEGV, 134/139) AFTER main returns, on every single run. stdout is
#    unaffected: 20/20 trials produced the sentinel and a 2001-line output arrived complete
#    through a pipe. Do NOT "fix" this with `System.exit()` in the dispatcher -- measured,
#    that trades the harmless post-main crash for an earlier one that loses output.
#
# 4. STDERR IS DISCARDED FROM THE OBSERVATION STREAM. It carries the shutdown crash's
#    register dump and backtrace on every run, which would swamp any real diagnostic. It is
#    kept in a file so a genuinely broken run can still be read: see the path this script
#    prints on failure.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
OUT="$REPO/build/art-runner"
IMAGE="${ART_RUNNER_IMAGE:-simplify-art-runner:pinned}"
MODE="${ART_RUNNER_MODE:-int}"

if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
    echo "run-batch.sh: image '$IMAGE' not found. Build it first:" >&2
    echo "    tools/art-runner/build-image.sh" >&2
    exit 1
fi

case "$MODE" in
    int) ART_FLAGS=(-Xint) ;;
    aot)
        if [[ "${ART_RUNNER_ALLOW_UNSOUND_AOT:-0}" != "1" ]]; then
            echo "run-batch.sh: ART_RUNNER_MODE=aot is NOT an oracle -- this image's dex2oat" >&2
            echo "  silently drops the aput-object assignability check (see invariant 1)." >&2
            echo "  Set ART_RUNNER_ALLOW_UNSOUND_AOT=1 only to measure the divergence itself." >&2
            exit 2
        fi
        ART_FLAGS=()
        ;;
    *) echo "run-batch.sh: ART_RUNNER_MODE must be 'int' or 'aot', got '$MODE'" >&2; exit 2 ;;
esac

"$HERE/assemble.sh" >&2

# Fixture class names come from the `.class` directives, not from file names, so a
# renamed file cannot silently drop a fixture out of the batch.
#
# ONLY `Fx_*` is auto-invoked, and that filter is load-bearing rather than cosmetic.
# `fixtures/` also holds SUPPORT classes (`Probe_*`) that exist to be acted upon, not
# invoked -- e.g. the `<clinit>` witnesses. `RunFixtures` reaches a fixture through
# `Class.forName`, which INITIALIZES it ([JLS] 12.4.1 lists reflective calls as a
# trigger), so invoking a witness directly initializes the very class whose silence
# another fixture is measuring. Before this filter, a no-argument run did exactly that
# and `Fx_ConstClassDoesNotInitialize` stayed correct only because alphabetical order
# happened to put it first -- renaming either file would have silently inverted the
# experiment. An explicit argument still runs anything, including a `Probe_`, on purpose.
if [[ $# -gt 0 ]]; then
    FIXTURES=("$@")
else
    # `LC_ALL=C sort` -- byte order, not the caller's locale. This order IS the dispatch order and
    # therefore the golden's line order, and the in-JVM harness reproduces this enumeration with
    # Kotlin's `String` ordering (also by code unit). Under a UTF-8 locale GNU `sort` collates
    # case- and punctuation-sensitively, so the two would disagree the moment a fixture name mixed
    # case or added an underscore -- a cross-tool divergence in the FIXTURES, which is exactly what
    # this harness must never confuse for a runtime divergence. For today's all-`Fx_`-uppercase names
    # the orders coincide, so this pins the invariant without moving a line.
    mapfile -t FIXTURES < <(
        sed -n 's/^\.class[^L]*L\(Fx_[^;]*\);.*/\1/p' "$HERE"/fixtures/*.smali | LC_ALL=C sort
    )
fi
if [[ ${#FIXTURES[@]} -eq 0 ]]; then
    echo "run-batch.sh: no fixtures found in $HERE/fixtures" >&2
    exit 1
fi

# Container-side classpath. Both entries are comfortably over the 17-character floor.
CP_ENTRIES=(/work/art-dispatcher.dex /work/art-fixtures.dex)
for entry in "${CP_ENTRIES[@]}"; do
    if [[ ${#entry} -lt 17 ]]; then
        echo "run-batch.sh: classpath entry '$entry' is ${#entry} chars; ART needs >= 17" >&2
        echo "  (oat_file_assistant.cc underflows its framework-res.apk suffix compare)" >&2
        exit 1
    fi
done
CLASSPATH="$(IFS=:; echo "${CP_ENTRIES[*]}")"

STDERR_LOG="$OUT/last-run.stderr"
STDOUT_LOG="$OUT/last-run.stdout"

set +e
docker run --rm --network=none \
    -v "$OUT:/work:ro" \
    "$IMAGE" "${ART_FLAGS[@]}" -cp "$CLASSPATH" RunFixtures "${FIXTURES[@]}" \
    >"$STDOUT_LOG" 2>"$STDERR_LOG"
set -e

if ! grep -qx 'DISPATCH_END' "$STDOUT_LOG"; then
    echo "run-batch.sh: sentinel 'DISPATCH_END' missing -- the batch did NOT complete." >&2
    echo "  stdout: $STDOUT_LOG" >&2
    echo "  stderr: $STDERR_LOG" >&2
    exit 1
fi

# Everything up to and including the sentinel. Anything after it is not an observation.
sed -n '1,/^DISPATCH_END$/p' "$STDOUT_LOG"
