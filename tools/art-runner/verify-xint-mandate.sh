#!/usr/bin/env bash
# Re-proves the `-Xint` mandate by measurement, on demand.
#
#     tools/art-runner/verify-xint-mandate.sh
#
# THE MANDATE: every oracle run passes `-Xint`, because this image's AOT compiler is
# demonstrably unsound and therefore is not a Dalvik oracle.
#
# THE EVIDENCE, and this script regenerates it rather than citing it: run
# `Fx_AputObjectIncompatible` -- which stores a `java.lang.Object` into a genuine
# `[Ljava.lang.String;` -- both ways.
#
#     -Xint    -> ArrayStoreException, which is what a real device (API 30) does
#     default  -> the store SUCCEEDS and the element reads back as `java.lang.Object`
#
# WHY THIS IS A PERMANENT SCRIPT AND NOT A ONE-OFF NOTE: the mandate is a claim about a
# pinned third-party artifact. If the pin is ever bumped, exactly one of two things is
# true -- the unsoundness persists (mandate still needed) or it was fixed (mandate can be
# relaxed, and that is a decision a human should make deliberately). A note in a README
# cannot tell you which; this script can. It FAILS if the divergence stops reproducing,
# so bumping the pin without re-examining the mandate is not a silent operation.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FIXTURE=Fx_AputObjectIncompatible

echo "==> $FIXTURE under -Xint (the mandated mode)"
INT_LINE="$(ART_RUNNER_MODE=int "$HERE/run-batch.sh" "$FIXTURE" 2>/dev/null | grep "^FIXTURE	$FIXTURE	")"
echo "    $INT_LINE"

echo "==> $FIXTURE under default AOT flags (deliberately, to measure the divergence)"
AOT_LINE="$(ART_RUNNER_MODE=aot ART_RUNNER_ALLOW_UNSOUND_AOT=1 "$HERE/run-batch.sh" "$FIXTURE" 2>/dev/null | grep "^FIXTURE	$FIXTURE	")"
echo "    $AOT_LINE"

fail=0

case "$INT_LINE" in
    *"THREW	java.lang.ArrayStoreException"*) ;;
    *)
        echo "FAIL: -Xint did NOT throw ArrayStoreException." >&2
        echo "  Either the image changed or the interpreter regressed. Until this is" >&2
        echo "  explained, treat Tier A results as unvalidated." >&2
        fail=1
        ;;
esac

case "$AOT_LINE" in
    *"THREW	java.lang.ArrayStoreException"*)
        echo "NOTE: the AOT tier now throws too -- the measured unsoundness NO LONGER" >&2
        echo "  reproduces. That is good news, but it means this script's evidence for the" >&2
        echo "  mandate is stale. Re-measure before relaxing anything: the mandate also" >&2
        echo "  rests on dex2oat being Android 10/11-vintage, which one fixed check does" >&2
        echo "  not change." >&2
        fail=1
        ;;
    *"NO_ASE"*) ;;
    *)
        echo "FAIL: the AOT run neither threw ASE nor reported NO_ASE. Unexpected third" >&2
        echo "  outcome -- read it before drawing any conclusion." >&2
        fail=1
        ;;
esac

if [[ $fail -eq 0 ]]; then
    echo "==> OK: -Xint throws, AOT silently accepts. The mandate stands, by measurement."
else
    exit 1
fi
