#!/usr/bin/env bash
# Cross-validates Tier B (this tool: real API-36 device ART) against the Tier A golden,
# and pins the Tier B capture so a device/image change cannot silently re-answer a settled
# question.
#
#     tools/emulator/cross-validate.sh            # measure + compare; print the agreement numbers
#     tools/emulator/cross-validate.sh --update   # re-record golden/tier-b-batch.txt
#
# WHAT THIS ANSWERS, and why it is worth a periodic 60s device run. Tier A is a bare
# `art_standalone` of Android-10/11 vintage; Tier B is a stock API-36 device. If a
# verifier-VALID opcode produces different bytes on the two, the difference is either the
# ART VERSION or the INTERPRETER TIER, and only measuring both modes tells them apart. So
# this runs Tier B TWICE:
#   - default (nterp): the modern device default and the tier art-runner cannot reach.
#   - -Xint (switch):  the apples-to-apples mode against Tier A's -Xint golden.
# Their diff isolates the interpreter tier; the golden-vs-default diff is the ART-version
# question. Both are printed as a NUMBER, because "Tier B agrees" is not a result.
#
# THREE KINDS OF DIFFERENCE, and only the third is a finding:
#   1. android.* boundary probes. `art_standalone` has NO framework classes, a real device
#      has the pure-Java ones and JNI-stubs the rest. The three `Fx_Android*` fixtures exist
#      to pin exactly that boundary (README.md), so they are EXPECTED to differ here and are
#      listed, not flagged. This is a classpath difference, not a Dalvik-semantics one.
#   2. exception MESSAGE drift. ART rewords diagnostics between versions (a `VerifyError`
#      offset, a `dlopen` vs `PathClassLoader` phrasing). Constraint 3 / README.md: assert on
#      the throwable TYPE and the value, never the message. So the semantic comparison DROPS
#      the message from every THREW record before diffing; a message-only change is not a
#      divergence.
#   3. a TYPE, VALUE, or throw-vs-not change on a non-android fixture. That is a real
#      cross-tier divergence and this script exits nonzero on it.
#
# NORMALIZATION is byte-identical to check-golden.sh (drop the `declaration of ... appears in
# <dex>` suffix, then map any `*.dex` path to `<DEX>`), so tier-b-batch.txt diffs directly
# against tier-a-batch.txt. Do NOT diverge the two; a golden that normalizes differently from
# its sibling cannot be compared against it.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
RUN_DEX="$HERE/run-dex.sh"
GOLDEN_A="$REPO/tools/art-runner/golden/tier-a-batch.txt"
GOLDEN_B="$HERE/golden/tier-b-batch.txt"
CONTAINER="${EMULATOR_CONTAINER:-simplify-emulator}"

# The three fixtures whose whole job is to pin the framework boundary (README.md). A
# difference on ANY OTHER fixture is a finding; a difference on these is the documented,
# expected classpath gap between a bare ART and a real device.
ANDROID_BOUNDARY='Fx_AndroidBase64|Fx_AndroidBuildModel|Fx_AndroidBuildModelAfterLoadLibrary'

UPDATE=0
case "${1:-}" in
    "") ;;
    --update) UPDATE=1 ;;
    *) echo "cross-validate.sh: unknown argument '$1' (expected --update or nothing)" >&2; exit 2 ;;
esac

# Identical to check-golden.sh's normalize(): rule 1 removes the whole `declaration of`
# parenthetical (path and all), rule 2 maps any surviving load-bearing dex path to <DEX>.
normalize() {
    sed -e "s/ (declaration of '[^']*' appears in [^)]*)//g" \
        -e 's#/[^ "]*\.dex#<DEX>#g'
}

# The golden's leading `#` provenance block is not an observation, so it is not compared.
body() { sed -n '/^[^#]/,$p' "$1"; }

# The SEMANTIC projection: keep OK records whole (class AND value both matter -- an
# `Integer(1)` that becomes `Integer(0)` is a real divergence), but truncate every THREW
# record to its throwable class, dropping the message. Everything else (RUNTIME, the
# interleaved CLINIT line, DISPATCH_END) passes through verbatim. Diffing two projections
# therefore surfaces type/value/throw-vs-not changes and hides message-only drift.
project() {
    awk -F'\t' 'BEGIN{OFS="\t"} $3=="THREW"{print $1,$2,$3,$4; next} {print}'
}

# Fixture names on the changed lines of a diff between two bodies, de-duplicated. Both bodies
# carry the same fixtures in the same dispatch order, so a changed cell shows up as a matched
# `<`/`></` pair under one name.
changed_fixtures() {
    # `diff` exits 1 on any difference, which under `set -o pipefail` would fail the whole
    # pipeline and (with `set -e`) abort the caller. Here a difference is the DATA, so the
    # brace group swallows that exit before the pipe.
    { diff <(printf '%s\n' "$1") <(printf '%s\n' "$2") || true; } \
        | sed -n 's/^[<>] FIXTURE\t\([^\t]*\)\t.*/\1/p' | sort -u
}

# Count non-empty lines in a string, robustly under `set -e` (empty -> 0, `grep -c`'s
# exit-1-on-no-match neutralised, no `&&`-idiom that would itself trip `set -e`).
count_lines() {
    if [[ -z "$1" ]]; then echo 0; else printf '%s\n' "$1" | grep -c . || true; fi
}

if [[ "$(docker ps -a --filter "name=^${CONTAINER}$" --format '{{.State}}' 2>/dev/null)" != "running" ]]; then
    echo "cross-validate.sh: container '$CONTAINER' is not running. Start it first:" >&2
    echo "    tools/emulator/start.sh" >&2
    exit 2
fi

# Force one fresh assemble so BOTH device runs -- and the Kotlin engine -- diff the same
# bytes at the same DEX_API_LEVEL. run-dex.sh reassembles too, but only on mtime; this closes
# the window where a bare API-level bump would leave a stale dex (assemble.sh's Task-3 fix).
"$REPO/tools/art-runner/assemble.sh" --force >&2

echo "==> running Tier B, API 36, default (nterp)" >&2
# EMULATOR_ART_FLAGS='' (not merely unset) so an inherited -Xint in the caller's environment
# cannot quietly turn this "default" run into a switch-interpreter run.
DEFAULT_OUT="$(EMULATOR_ART_FLAGS='' "$RUN_DEX" 2>/dev/null | normalize || true)"
echo "==> running Tier B, API 36, -Xint (switch)" >&2
XINT_OUT="$(EMULATOR_ART_FLAGS=-Xint "$RUN_DEX" 2>/dev/null | normalize || true)"

# The sentinel, not the exit status, is what proves a batch ran to completion. A missing one
# means the device run broke; re-run run-dex.sh directly (without 2>/dev/null) to see why.
if ! grep -qx 'DISPATCH_END' <<<"$DEFAULT_OUT" || ! grep -qx 'DISPATCH_END' <<<"$XINT_OUT"; then
    echo "cross-validate.sh: a batch did not reach DISPATCH_END -- nothing to compare." >&2
    echo "    EMULATOR_ART_FLAGS= tools/emulator/run-dex.sh    # run it directly to see the error" >&2
    exit 2
fi

CELLS="$(grep -c '^FIXTURE	' <<<"$DEFAULT_OUT")"

if [[ $UPDATE -eq 1 ]]; then
    mkdir -p "$(dirname "$GOLDEN_B")"
    API="$(docker exec "$CONTAINER" adb -e shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r\n')"
    REL="$(docker exec "$CONTAINER" adb -e shell getprop ro.build.version.release 2>/dev/null | tr -d '\r\n')"
    BUILD="$(docker exec "$CONTAINER" adb -e shell getprop ro.build.id 2>/dev/null | tr -d '\r\n')"
    XINT_NOTE="byte-identical to this capture on the recording run"
    if [[ "$DEFAULT_OUT" != "$XINT_OUT" ]]; then
        XINT_NOTE="DIFFERED from this capture -- see cross-validate.sh output"
    fi
    {
        echo "# The Tier B batch's answers, pinned. Regenerate: tools/emulator/cross-validate.sh --update"
        echo "# Same normalization as tools/art-runner/check-golden.sh, so this diffs directly against"
        echo "# tools/art-runner/golden/tier-a-batch.txt. This header is not compared."
        echo "#"
        echo "# recorded: $(date -u +%Y-%m-%d)"
        echo "# mode: default (nterp) -- the modern device default and the tier art-runner cannot reach"
        echo "# device: Android $REL / API $API, build $BUILD (real emulator, tools/emulator/)"
        echo "# -Xint: $XINT_NOTE"
        printf '%s\n' "$DEFAULT_OUT"
    } > "$GOLDEN_B"
    echo "cross-validate.sh: recorded $CELLS fixture records to $GOLDEN_B (default/nterp, API $API)"
    exit 0
fi

if [[ ! -f "$GOLDEN_A" ]]; then
    echo "cross-validate.sh: no Tier A golden at $GOLDEN_A to compare against." >&2
    exit 2
fi
GOLDEN_A_BODY="$(body "$GOLDEN_A")"

# --- Comparison 1: interpreter tiers, same device. default (nterp) vs -Xint (switch). ---
# Empty diff means the nterp/switch tiers agree cell-for-cell, so any Tier-A disagreement
# below is the ART VERSION, not the tier.
TIER_DIFFS="$(changed_fixtures "$DEFAULT_OUT" "$XINT_OUT")"
TIER_N="$(count_lines "$TIER_DIFFS")"
echo "==> Tier B interpreter tiers  (nterp vs -Xint, API 36)"
echo "    $((CELLS - TIER_N)) of $CELLS cells identical"
if [[ $TIER_N -gt 0 ]]; then
    echo "    tiers DISAGREE on:" ; printf '%s\n' "$TIER_DIFFS" | sed 's/^/      /'
    echo "    (a verifier-valid nterp-vs-switch divergence is Task-9-adjacent; record it)"
fi

# --- Comparison 2: Tier A -Xint (golden) vs Tier B default (nterp). ---
# RAW first (message drift included), then SEMANTIC (message dropped). The gap between the
# two numbers is exactly the message-only ART wording drift.
RAW_DIFFS="$(changed_fixtures "$GOLDEN_A_BODY" "$DEFAULT_OUT")"
RAW_N="$(count_lines "$RAW_DIFFS")"
SEM_DIFFS="$(changed_fixtures "$(project <<<"$GOLDEN_A_BODY")" "$(project <<<"$DEFAULT_OUT")")"
SEM_N="$(count_lines "$SEM_DIFFS")"

echo "==> Tier A -Xint (golden) vs Tier B default (nterp, API 36)"
echo "    raw:      $((CELLS - RAW_N)) of $CELLS cells byte-identical"
echo "    semantic: $((CELLS - SEM_N)) of $CELLS cells agree on TYPE+VALUE (message drift ignored)"

# Of the semantic divergences, split the expected framework-boundary probes from any real one.
REAL="$(printf '%s\n' "$SEM_DIFFS" | grep -Ev "^($ANDROID_BOUNDARY)$" || true)"
BOUNDARY="$(printf '%s\n' "$SEM_DIFFS" | grep -E "^($ANDROID_BOUNDARY)$" || true)"
if [[ -n "$BOUNDARY" ]]; then
    echo "    android.* boundary (expected classpath gap, not a Dalvik divergence):"
    printf '%s\n' "$BOUNDARY" | sed 's/^/      /'
fi

# The -Xint mandate, re-derived on Tier B: the device default must throw ASE on the canary,
# agreeing with Tier A -Xint and contradicting Tier A's unsound AOT.
ASE_LINE="$(grep '^FIXTURE	Fx_AputObjectIncompatible	' <<<"$DEFAULT_OUT" || true)"
case "$ASE_LINE" in
    *"THREW	java.lang.ArrayStoreException"*)
        echo "==> -Xint mandate holds: Tier B default THROWS ArrayStoreException on the aput-object"
        echo "    canary, matching Tier A -Xint and contradicting Tier A's AOT (which drops it)." ;;
    *)
        echo "cross-validate.sh: Fx_AputObjectIncompatible did NOT throw ASE on the device default:" >&2
        echo "    $ASE_LINE" >&2
        echo "  The -Xint mandate's premise no longer reproduces -- investigate before trusting." >&2
        exit 1 ;;
esac

if [[ -n "$REAL" ]]; then
    echo "FAIL: a real cross-tier divergence on a non-android fixture:" >&2
    printf '%s\n' "$REAL" | sed 's/^/    /' >&2
    echo "  This is a TYPE/VALUE/throw-vs-not change between Tier A -Xint and Tier B default." >&2
    echo "  It outranks everything in the plan -- read both answers before accepting." >&2
    exit 1
fi

echo "==> OK: every non-android cell agrees on type+value across the ART version gap."
