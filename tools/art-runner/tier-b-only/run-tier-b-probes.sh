#!/usr/bin/env bash
# Crash-tolerant runner for the tier-B register-read probes.
#
# THIS IS THE OPPOSITE OF run-batch.sh's DESIGN, DELIBERATELY. run-batch.sh runs a whole
# batch inside ONE dalvikvm and gates on a sentinel, because there a lost batch is cheap to
# re-run. Here a lost run is a LOST ANSWER: nterp dereferencing a non-zero primitive is
# expected to die on a fatal native signal, so every probe gets its OWN dalvikvm process
# (per-probe isolation), a timeout, and "the VM died here (signal N)" is a FIRST-CLASS
# RECORDED OUTCOME rather than a harness failure. The script exits 0 as long as it recorded
# each probe's fate; only infrastructure failures (container down, assembly failed) are errors.
#
# The -Xint mandate is suspended for the `default`/`nojit` modes on purpose: -Xint selects the
# switch interpreter, and these probes must reach nterp, which only the default selects. The `xint`
# mode is the switch-tier comparison.
#
# All three modes add -Xverify:none, because the probes are VERIFIER-INVALID by construction
# (a non-reference register in a type op is VERIFY_ERROR_BAD_CLASS_HARD, which rejects the whole
# class) and could not otherwise load.
#
# Modes:
#   default        (no -Xint)              -Xverify:none   # nterp target -- DEFAULT flags
#   nojit          -Xusejit:false          -Xverify:none   # nterp, JIT removed as a confound
#   xint           -Xint                   -Xverify:none   # switch interpreter, for the diff
#
# Usage: tools/art-runner/tier-b-only/run-tier-b-probes.sh
set -uo pipefail   # NOT -e: a crashing probe must not abort the sweep.

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../../.." && pwd)"
OUT="$REPO/build/emulator/tier-b"
CONTAINER="${EMULATOR_CONTAINER:-simplify-emulator}"
DEVICE_DIR="${EMULATOR_DEVICE_DIR:-/data/local/tmp}"
DEVICE_DEX="$DEVICE_DIR/tier-b-probes.dex"
TIMEOUT="${TIER_B_TIMEOUT:-40}"
CONST_VALUE="0x11223344"   # the distinctive non-zero primitive the probes store, for the report

PROBES=(
    TierB_InstanceOfNonZeroPrim
    TierB_CheckCastNonZeroPrim
    TierB_InstanceOfSmallPrim
    TierB_InstanceOfHighPrim
    TierB_InstanceOfZeroPrim
    TierB_CheckCastZeroPrim
    TierB_InstanceOfRealObject
)
# Named modes -> dalvikvm flag sets (excluding the shared -Xverify:none, added below).
MODE_NAMES=(default nojit xint)
declare -A MODE_FLAGS=(
    [default]=""
    [nojit]="-Xusejit:false"
    [xint]="-Xint"
)

die() { echo "run-tier-b-probes.sh: $*" >&2; exit 1; }

# --- preflight -------------------------------------------------------------------------------
[[ "$(docker ps -a --filter "name=^${CONTAINER}$" --format '{{.State}}')" == "running" ]] \
    || die "container '$CONTAINER' is not running (tools/emulator/start.sh)"
[[ "$(docker exec "$CONTAINER" adb -e shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n')" == "1" ]] \
    || die "'$CONTAINER' is up but Android has not finished booting"
docker exec "$CONTAINER" adb -e root >/dev/null 2>&1   # tombstones/logcat need root; idempotent

mkdir -p "$OUT"

# --- assemble the probes into ONE dex, our OWN way (never through fixtures/) -----------------
API_LEVEL="$(sed -n 's/.*DEX_API_LEVEL *= *\([0-9]\+\).*/\1/p' \
    "$REPO/smalivm/src/main/java/org/cf/smalivm/dex/SmaliParser.kt" | head -1)"
[[ "$API_LEVEL" =~ ^[0-9]+$ ]] || die "could not read DEX_API_LEVEL"
# Resolve through Gradle on every invocation, just like assemble.sh; never reuse an
# assembler classpath saved before a dependency upgrade.
echo "resolving the smali classpath via Gradle" >&2
SMALI_CP="$("$REPO/gradlew" -q -p "$REPO" :smalivm:printSmaliAssemblerClasspath \
    | grep -F 'smali-3.0.10.jar' | tail -1)" \
    || die "could not resolve a classpath containing smali-3.0.10.jar"

LOCAL_DEX="$OUT/tier-b-probes.dex"
echo "assembling tier-B probes -> $LOCAL_DEX (api $API_LEVEL)" >&2
java -cp "$SMALI_CP" com.android.tools.smali.smali.Main assemble \
    --api "$API_LEVEL" --jobs 1 --output "$LOCAL_DEX" "$HERE"/*.smali \
    || die "smali assembly failed"

docker cp "$LOCAL_DEX" "$CONTAINER:/tmp/tier-b-probes.dex" >/dev/null || die "docker cp failed"
docker exec "$CONTAINER" adb -e push "/tmp/tier-b-probes.dex" "$DEVICE_DEX" >/dev/null \
    || die "adb push failed"

# --- run one probe under one mode, crash-tolerant -------------------------------------------
# Records every artifact and classifies the outcome. Returns nothing meaningful; writes files.
run_one() {
    local mode="$1" cls="$2"
    local flags="${MODE_FLAGS[$mode]}"
    local base="$OUT/${mode}__${cls}"
    local so="$base.stdout" se="$base.stderr" lc="$base.logcat" meta="$base.meta" tomb="$base.tombstone"
    : > "$so"; : > "$se"; : > "$lc"; : > "$meta"; : > "$tomb"

    # A clean logcat slate so the captured crash lines belong to THIS run only.
    docker exec "$CONTAINER" adb -e logcat -c -b main -b crash -b system >/dev/null 2>&1
    docker exec "$CONTAINER" adb -e shell 'rm -f /data/tombstones/tombstone_* 2>/dev/null' >/dev/null 2>&1

    # One dalvikvm, this probe only, wrapped in a timeout. -e is docker-side; the inner timeout
    # guards a hang on the device. We do NOT pipe dalvikvm's stdout so its exit status is direct.
    timeout "$TIMEOUT" docker exec "$CONTAINER" adb -e shell \
        dalvikvm $flags -Xverify:none -cp "$DEVICE_DEX" "$cls" >"$so" 2>"$se"
    local rc=$?

    tr -d '\r' < "$so" > "$so.clean" && mv "$so.clean" "$so"
    tr -d '\r' < "$se" > "$se.clean" && mv "$se.clean" "$se"

    # Crash evidence lives in logcat, not on the adb channel. Pull the crash+main buffers.
    docker exec "$CONTAINER" adb -e logcat -d -b main -b crash -b system >"$lc" 2>/dev/null
    tr -d '\r' < "$lc" > "$lc.clean" && mv "$lc.clean" "$lc"

    # Pull any tombstone the crash produced (full symbolicated-ish backtrace).
    local tfile
    tfile="$(docker exec "$CONTAINER" adb -e shell 'ls -1 /data/tombstones/tombstone_* 2>/dev/null | head -1' | tr -d '\r')"
    if [[ -n "$tfile" ]]; then
        docker exec "$CONTAINER" adb -e shell "cat '$tfile'" >"$tomb" 2>/dev/null
        tr -d '\r' < "$tomb" > "$tomb.clean" && mv "$tomb.clean" "$tomb"
    fi

    # Extract the load-bearing signals.
    local fatal fault backtrace_hit outcome
    fatal="$(grep -aoE 'Fatal signal [0-9]+ \([A-Z]+\)[^\n]*' "$lc" | head -1)"
    fault="$(grep -aoE 'fault addr (0x)?[0-9a-fA-F]+' "$lc" "$tomb" 2>/dev/null | grep -aoE '(0x)?[0-9a-fA-F]+$' | head -1)"
    # nterp vs switch, named at the crash site if symbols survived.
    if grep -aqE 'ExecuteNterpImpl|nterp_op_|ExecuteNterp|art_quick_.*nterp' "$lc" "$tomb" 2>/dev/null; then
        backtrace_hit="nterp"
    elif grep -aqE 'ExecuteSwitchImpl|interpreter_switch' "$lc" "$tomb" 2>/dev/null; then
        backtrace_hit="switch"
    else
        backtrace_hit="(no interpreter frame symbolicated)"
    fi

    if [[ $rc -eq 124 ]]; then
        outcome="TIMEOUT (>${TIMEOUT}s) -- VM did not terminate"
    elif [[ -n "$fatal" ]]; then
        outcome="CRASH -- $fatal${fault:+ ; fault addr $fault}"
    elif grep -aq 'TIERB_CHECKCAST_SURVIVED' "$so"; then
        outcome="SURVIVED (check-cast) -- $(grep -a TIERB "$so" | tail -1)"
    elif grep -aq 'TIERB_INSTANCEOF_RESULT=' "$so"; then
        outcome="RETURNED -- $(grep -a 'TIERB_INSTANCEOF_RESULT=' "$so" | head -1)"
    elif [[ -s "$se" ]] && grep -aqiE 'exception|error|throw' "$se"; then
        outcome="THREW -- $(grep -aiE 'exception|error|throw' "$se" | head -1)"
    elif grep -aq 'TIERB_BEGIN' "$so"; then
        outcome="INDETERMINATE -- reached op, no result/crash line (rc=$rc)"
    else
        outcome="NO_OUTPUT (rc=$rc) -- probe did not print its BEGIN marker"
    fi

    {
        echo "mode=$mode class=$cls flags='$flags -Xverify:none'"
        echo "rc=$rc"
        echo "outcome=$outcome"
        echo "fatal=${fatal:-(none)}"
        echo "fault_addr=${fault:-(none)}"
        echo "interpreter_frame=${backtrace_hit}"
        echo "tombstone=$([[ -s "$tomb" ]] && echo "$tomb" || echo '(none)')"
        echo "stdout_tail=$(grep -a TIERB "$so" | tr '\n' '|')"
    } > "$meta"

    printf '  %-7s %-30s -> %s [frame:%s]\n' "$mode" "$cls" "$outcome" "$backtrace_hit"
}

echo "=== Tier-B register-read probes (const $CONST_VALUE), API $API_LEVEL, per-probe isolation ==="
for mode in "${MODE_NAMES[@]}"; do
    echo "--- mode: $mode (flags: '${MODE_FLAGS[$mode]} -Xverify:none') ---"
    for cls in "${PROBES[@]}"; do
        run_one "$mode" "$cls"
    done
done

echo
echo "Artifacts (stdout/stderr/logcat/tombstone/meta) under: $OUT"
exit 0
