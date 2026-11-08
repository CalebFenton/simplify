#!/usr/bin/env bash
# Runs a dex on the booted emulator's `dalvikvm` and prints what it did.
#
#     tools/emulator/run-dex.sh                          # THE DEFAULT: the art-runner
#                                                        # fixture batch, on API 36
#     tools/emulator/run-dex.sh foo.dex SomeClass        # one class from one dex
#     tools/emulator/run-dex.sh a.dex:b.dex Cls arg1     # multi-dex cp, trailing args pass
#
# NO APK IS INVOLVED, and that is the point worth internalising: `dalvikvm -cp foo.dex Class`
# runs on a stock API 36 device image with no manifest, no packaging, no signing, no install.
# Verified end to end. It means the art-runner dispatcher and fixtures port here UNCHANGED,
# which is what makes the cross-tier diff below free.
#
# THE DEFAULT MODE IS THE HIGH-VALUE ONE. With no arguments this runs the EXISTING
# tools/art-runner/ fixture corpus -- same dexes, same dispatcher, same fixture list, same
# order -- so its output diffs directly against `tools/art-runner/run-batch.sh`. Two very
# different ARTs agreeing on a fixture is real corroboration; disagreeing is a FINDING about
# ART tiers, not a bug to paper over. See README.md, "The cross-tier diff".
#
# Environment:
#     EMULATOR_CONTAINER    container name (default simplify-emulator)
#     EMULATOR_ART_FLAGS    extra dalvikvm flags. DEFAULT IS EMPTY, DELIBERATELY.
#     EMULATOR_DEVICE_DIR   where dexes land (default /data/local/tmp)
#
# WHY `EMULATOR_ART_FLAGS` DEFAULTS TO EMPTY, when art-runner MANDATES `-Xint`:
#
#   art-runner mandates `-Xint` because ITS dex2oat is Android 10/11-vintage and measurably
#   unsound (it drops the `aput-object` assignability check). That reasoning does not transfer.
#   Here, default flags ARE the oracle: default flags are what a real app gets, and default
#   flags are the ONLY way to reach `nterp` -- the tier art-runner does not contain, and the
#   single biggest reason this tool exists. Forcing `-Xint` here would reproduce art-runner's
#   blind spot at 100x the cost.
#
#   `EMULATOR_ART_FLAGS=-Xint tools/emulator/run-dex.sh` is still worth running: it isolates
#   whether a cross-tier disagreement comes from the TIER or from the ANDROID VERSION.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
ART_RUNNER="$REPO/tools/art-runner"
OUT="$REPO/build/emulator"
CONTAINER="${EMULATOR_CONTAINER:-simplify-emulator}"
DEVICE_DIR="${EMULATOR_DEVICE_DIR:-/data/local/tmp}"
# shellcheck disable=SC2206
ART_FLAGS=(${EMULATOR_ART_FLAGS:-})

# `docker ps --filter` rather than `docker inspect`: on Docker 29.7.1 `docker inspect -f` on a
# missing container prints a bare newline to STDOUT and exits 1, so the idiomatic
# `$(... || echo absent)` yields $'\nabsent' and compares equal to nothing. See start.sh.
if [[ "$(docker ps -a --filter "name=^${CONTAINER}$" --format '{{.State}}')" != "running" ]]; then
    echo "run-dex.sh: container '$CONTAINER' is not running. Start it first:" >&2
    echo "    tools/emulator/start.sh" >&2
    exit 1
fi
if [[ "$(docker exec "$CONTAINER" adb -e shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n')" != "1" ]]; then
    echo "run-dex.sh: '$CONTAINER' is up but Android has not finished booting." >&2
    echo "    tools/emulator/start.sh    # idempotent; it will wait on the boot gate" >&2
    exit 1
fi

mkdir -p "$OUT"

BATCH_MODE=0
if [[ $# -eq 0 ]]; then
    BATCH_MODE=1
    # Calling art-runner's assembler is fine and intended; EDITING it is not. It is the single
    # source of truth for the DEX API level (read out of SmaliParser.DEX_API_LEVEL, currently
    # 20 => dex 035). Modern ART accepts 035 through 041, so these load here unchanged -- and
    # that claim is checked by this script actually running them, not asserted.
    "$ART_RUNNER/assemble.sh" >&2
    LOCAL_DEXES=("$REPO/build/art-runner/art-dispatcher.dex" "$REPO/build/art-runner/art-fixtures.dex")
    MAIN_CLASS="RunFixtures"
    # Identical derivation and identical sort order to run-batch.sh, because a differently
    # ordered batch does not diff against one. That includes the `Fx_` filter: `fixtures/`
    # also holds SUPPORT classes (`Probe_*`) meant to be acted upon rather than invoked,
    # and reaching one through `Class.forName` INITIALIZES it ([JLS] 12.4.1), which
    # corrupts any fixture measuring whether something else initialized it. See
    # run-batch.sh's comment for the near-miss this filter closes.
    mapfile -t PROGRAM_ARGS < <(
        sed -n 's/^\.class[^L]*L\(Fx_[^;]*\);.*/\1/p' "$ART_RUNNER"/fixtures/*.smali | sort
    )
    if [[ ${#PROGRAM_ARGS[@]} -eq 0 ]]; then
        echo "run-dex.sh: no fixtures found in $ART_RUNNER/fixtures" >&2
        exit 1
    fi
elif [[ $# -eq 1 ]]; then
    echo "run-dex.sh: given a dex but no class name." >&2
    echo "    usage: run-dex.sh <dex[:dex...]> <MainClass> [args...]" >&2
    echo "    or no arguments at all, to run the art-runner fixture batch" >&2
    exit 2
else
    IFS=':' read -r -a LOCAL_DEXES <<< "$1"
    MAIN_CLASS="$2"
    shift 2
    PROGRAM_ARGS=("$@")
fi

# Two hops -- `docker cp` then `adb push` -- rather than a bind mount, so any host path works
# and `start.sh` does not have to guess at build time which directories will matter later. Both
# hops move kilobytes.
DEVICE_CP_ENTRIES=()
for local_dex in "${LOCAL_DEXES[@]}"; do
    if [[ ! -f "$local_dex" ]]; then
        echo "run-dex.sh: no such dex: $local_dex" >&2
        exit 1
    fi
    base="$(basename "$local_dex")"
    device_path="$DEVICE_DIR/$base"
    # The >=17 character floor is invariant 2 in art-runner's run-batch.sh: a size_t underflow in
    # oat_file_assistant.cc's `framework-res.apk` suffix compare aborts on shorter paths.
    #
    # MEASURED: THAT BUG IS FIXED ON API 36. `/sdcard/d.dex` -- 13 characters, well under the
    # floor -- loaded and ran correctly here. So this assert is NOT protecting against a live
    # failure; it is kept because /data/local/tmp paths clear the floor for free, and because a
    # dex path that works on one tool and aborts on the other is a difference nobody should
    # rediscover by bisection. Cheap invariant, and it keeps the two tools' inputs interchangeable.
    if [[ ${#device_path} -lt 17 ]]; then
        echo "run-dex.sh: device path '$device_path' is ${#device_path} chars; keep it >= 17" >&2
        exit 1
    fi
    docker cp "$local_dex" "$CONTAINER:/tmp/$base"
    docker exec "$CONTAINER" adb -e push "/tmp/$base" "$device_path" >/dev/null
    DEVICE_CP_ENTRIES+=("$device_path")
done
DEVICE_CP="$(IFS=:; echo "${DEVICE_CP_ENTRIES[*]}")"

STDOUT_LOG="$OUT/last-run.stdout"
STDERR_LOG="$OUT/last-run.stderr"

# `dalvikvm` bare, not an absolute path. On API 36 there is NO /system/bin/dalvikvm at all --
# it lives at /apex/com.android.art/bin/dalvikvm (a symlink to dalvikvm64) and that APEX bin
# directory is on the device shell's PATH. Hardcoding /system/bin/dalvikvm is a stale
# instinct from older API levels and fails here.
#
# stdout and stderr stay SEPARATED across the adb channel (verified), and stderr turns out to be
# entirely EMPTY on a clean run -- measured 0 bytes. ART's startup chatter (`Using
# CollectorTypeCMC GC`, the nativeloader lines, the ICU/tzdata path notices) goes to LOGCAT
# ONLY, not to stderr. That is the opposite of art-runner, where stderr carries a register dump
# and a backtrace on every single run. So a non-empty stderr here is a genuine signal worth
# reading rather than noise to be filtered: `adb -e logcat -d` is where the chatter lives.
set +e
docker exec "$CONTAINER" adb -e shell \
    dalvikvm "${ART_FLAGS[@]}" -cp "$DEVICE_CP" "$MAIN_CLASS" "${PROGRAM_ARGS[@]}" \
    >"$STDOUT_LOG" 2>"$STDERR_LOG"
RC=$?
set -e

# adb strips nothing but the device pty adds CR to every line. Left in the raw log, stripped
# for anything that gets compared -- a golden file full of invisible \r is a golden file that
# never matches.
tr -d '\r' < "$STDOUT_LOG" > "$STDOUT_LOG.clean" && mv "$STDOUT_LOG.clean" "$STDOUT_LOG"
tr -d '\r' < "$STDERR_LOG" > "$STDERR_LOG.clean" && mv "$STDERR_LOG.clean" "$STDERR_LOG"

if [[ $BATCH_MODE -eq 1 ]]; then
    # Gate on the sentinel, exactly as run-batch.sh does. Here the exit status happens to be
    # trustworthy (device ART does not have art_standalone's post-main shutdown crash), but the
    # sentinel is what proves the batch RAN TO COMPLETION rather than dying midway, and that is
    # a different question from whether the process exited cleanly.
    if ! grep -qx 'DISPATCH_END' "$STDOUT_LOG"; then
        echo "run-dex.sh: sentinel 'DISPATCH_END' missing -- the batch did NOT complete." >&2
        echo "  dalvikvm exit status: $RC" >&2
        echo "  stdout: $STDOUT_LOG" >&2
        echo "  stderr: $STDERR_LOG" >&2
        exit 1
    fi
    sed -n '1,/^DISPATCH_END$/p' "$STDOUT_LOG"
    exit 0
fi

cat "$STDOUT_LOG"
if [[ -s "$STDERR_LOG" ]]; then
    echo "run-dex.sh: stderr is in $STDERR_LOG" >&2
fi
exit "$RC"
