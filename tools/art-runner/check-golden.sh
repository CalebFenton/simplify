#!/usr/bin/env bash
# Compares the Tier A batch against a recorded golden, so a pin bump cannot silently
# re-answer a settled question.
#
#     tools/art-runner/check-golden.sh             # compare; nonzero exit on any mismatch
#     tools/art-runner/check-golden.sh --update    # re-record, once a human has accepted it
#
# WHY A GOLDEN AND NOT `git diff` ON A CAPTURED FILE. Every answer this corpus feeds into
# DALVIK-SEMANTICS.md cites a PINNED THIRD-PARTY ARTIFACT (`art_standalone` from Alpine
# edge/testing, see Dockerfile). Bumping that pin is a one-line edit whose effect on the
# answers is otherwise invisible: nothing fails, the numbers just quietly become different
# numbers. This script is the thing that fails.
#
# A MISMATCH HAS EXACTLY THREE CAUSES, and only the third is interesting:
#   1. the image changed        -> the version block below, which runs FIRST
#   2. a fixture changed        -> the diff, plus the list of records that moved
#   3. ART changed its answer   -> what is left once 1 and 2 are ruled out
# Exit status: 2 means cause 1 is in play, 1 means cause 2 or 3, 0 means no drift.
#
# CAUSE 1 IS TWO DIFFERENT SITUATIONS AND THEY ARE NOT HANDLED THE SAME WAY. Only the first
# of them is a reason to refuse to look:
#   - the Dockerfile pin and the image label DISAGREE. Nothing is comparable: the batch would
#     measure an artifact that is not the one under test. Stop, say "rebuild".
#   - the pin and the image AGREE and only the GOLDEN is older -- i.e. somebody bumped the
#     pin and rebuilt, the ordinary workflow. This IS comparable, so the batch runs and the
#     diff is printed, labelled with the version on each side. Exiting early here was a real
#     bug: "the image moved" does not say WHETHER an answer moved, and this script's own
#     acceptance procedure is "read the diff, THEN --update". With the diff withheld, the
#     only route left was `--update` blind followed by `git diff` -- exactly the workflow the
#     paragraph above argues against. The status is still 2, because cause 1 is confirmed
#     present and no script can know how much of the diff it explains.
#
# THIS FILE PINS ART'S EXACT MESSAGE TEXT, and that contradicts README.md's "don't pin ART
# message text" rule ON PURPOSE. That rule governs FIDELITY assertions ACROSS runtimes,
# where a diagnostic string is a version artifact and not a semantic. A golden is a DRIFT
# DETECTOR for ONE tool, which is a different job: an ART whose wording changed changed
# something, and noticing is the entire point. Do NOT "fix" this into message-stripping --
# that deletes the signal the file exists to carry.
#
# MODE: `-Xint` only, the mandated default (run-batch.sh invariant 1). The AOT tier of this
# image is demonstrably unsound, so a golden of it would pin a wrong answer as the truth.
#
# NORMALIZATION -- ONLY genuinely non-semantic churn, one rule per reason. A golden that
# churns for uninteresting reasons is a golden somebody deletes.
#   1. `(declaration of 'X' appears in <path>.dex)`, the suffix ART appends to verifier
#      diagnostics, is DROPPED: it names the dex a class was loaded from, which moves
#      whenever the harness reorganises its build outputs, and the class name it repeats
#      already appears twice earlier in the same diagnostic.
#   2. Any absolute `*.dex` path becomes `<DEX>`: the container-side mount path is a
#      harness detail, and it leaks into genuine diagnostics -- `UnsatisfiedLinkError`
#      renders the whole `DexPathList`, mount paths and all.
# NOTHING ELSE is normalized. In particular the `RUNTIME` line's `java.vm.version` IS
# COMPARED: an ART version change is precisely the event this file exists to catch, so
# normalizing it away would defeat the file.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GOLDEN="$HERE/golden/tier-a-batch.txt"
IMAGE="${ART_RUNNER_IMAGE:-simplify-art-runner:pinned}"
VER_LABEL=org.cf.simplify.art-standalone-version

UPDATE=0
IMAGE_MOVED=0
GOLDEN_VER=""
case "${1:-}" in
    "") ;;
    --update) UPDATE=1 ;;
    *) echo "check-golden.sh: unknown argument '$1' (expected --update or nothing)" >&2; exit 2 ;;
esac

# Rules 1 and 2 above, in that order: rule 1 removes the whole parenthetical including its
# path, so rule 2 only ever sees paths that are load-bearing parts of a message.
normalize() {
    sed -e "s/ (declaration of '[^']*' appears in [^)]*)//g" \
        -e 's#/[^ "]*\.dex#<DEX>#g'
}

# The golden's leading `#` block is provenance, not an observation, so it is not compared.
body() {
    sed -n '/^[^#]/,$p' "$1"
}

# Names the records a unified diff moved, so the reader does not have to parse the diff to
# learn which fixture to go look at. Not every changed line carries a fixture name: the
# `RUNTIME` line and anything a fixture printed itself (a `<clinit>`) do not.
report_moved() {
    local moved
    moved="$(printf '%s\n' "$1" | awk -F'\t' '/^[-+]FIXTURE\t/ { print $2 }' | sort -u)"
    if [[ -n "$moved" ]]; then
        echo "  Records that moved:" >&2
        printf '%s\n' "$moved" | sed 's/^/    /' >&2
    else
        echo "  No FIXTURE record moved, so the change is in the RUNTIME line or in a line a" >&2
        echo "  fixture printed itself (a <clinit>, say). Read the diff above." >&2
    fi
}

if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
    echo "check-golden.sh: image '$IMAGE' not found -- cannot compare anything." >&2
    echo "    tools/art-runner/build-image.sh" >&2
    exit 2
fi
IMAGE_VER="$(docker image inspect "$IMAGE" --format "{{index .Config.Labels \"$VER_LABEL\"}}")"
PINNED_VER="$(sed -n 's/^ARG ART_VER=//p' "$HERE/Dockerfile" | head -1)"

if [[ $UPDATE -eq 0 ]]; then
    if [[ ! -f "$GOLDEN" ]]; then
        echo "check-golden.sh: no golden at $GOLDEN. Record one with --update." >&2
        exit 2
    fi
    GOLDEN_VER="$(sed -n 's/^# art-standalone: //p' "$GOLDEN" | head -1)"

    echo "==> art_standalone version, three ways (image: $IMAGE)"
    printf '    %-26s %s\n' "golden recorded against" "${GOLDEN_VER:-<missing from golden header>}"
    printf '    %-26s %s\n' "Dockerfile pins" "${PINNED_VER:-<unreadable>}"
    printf '    %-26s %s\n' "the image actually has" "${IMAGE_VER:-<unlabelled>}"

    # THE PIN AND THE IMAGE DISAGREE -> nothing here is comparable, so stop. The batch would
    # run against an artifact that is not the one under test, and every line of the diff
    # would answer a question nobody asked.
    if [[ "$PINNED_VER" != "$IMAGE_VER" ]]; then
        echo "FAIL: the image on this host is not the pinned one, so there is nothing" >&2
        echo "  meaningful to compare. Rebuild and re-run:" >&2
        echo "    tools/art-runner/build-image.sh" >&2
        exit 2
    fi

    # THE PIN AND THE IMAGE AGREE BUT THE GOLDEN IS OLDER -> the maintainer bumped the pin
    # and rebuilt, which is the NORMAL workflow, and the artifact is perfectly comparable.
    # So the batch STILL RUNS and the diff is STILL PRINTED. Exiting here instead would be a
    # bug: "the image moved" does not say WHETHER an answer moved, and the acceptance
    # procedure is "read the diff, then --update". Refusing to print it leaves `--update`
    # blind-then-`git diff` as the only route, which is the workflow this script exists to
    # replace. The exit status stays 2 because cause 1 is confirmed present and this script
    # cannot know how much of the diff it explains -- that is the human's call.
    if [[ "$GOLDEN_VER" != "$IMAGE_VER" ]]; then
        IMAGE_MOVED=1
    fi
fi

OBSERVED="$(mktemp)"
RUN_ERR="$(mktemp)"
trap 'rm -f "$OBSERVED" "$RUN_ERR"' EXIT

# `ART_RUNNER_MODE=int` is set rather than defaulted, so an exported `aot` in the caller's
# environment cannot quietly compare the golden against a tier that is not an oracle.
# run-batch.sh's stderr is held back rather than discarded: it is assemble.sh chatter on a
# good run, but on a bad one it is the sentinel diagnostic and the path to the raw logs.
if ! ART_RUNNER_MODE=int ART_RUNNER_IMAGE="$IMAGE" "$HERE/run-batch.sh" 2>"$RUN_ERR" \
        | normalize > "$OBSERVED"; then
    echo "check-golden.sh: the batch did not complete, so there is nothing to compare." >&2
    cat "$RUN_ERR" >&2
    exit 2
fi

if [[ $UPDATE -eq 1 ]]; then
    mkdir -p "$(dirname "$GOLDEN")"
    {
        echo "# The Tier A batch's answers, pinned. Regenerate: tools/art-runner/check-golden.sh --update"
        echo "# Compared after the normalization documented in check-golden.sh; this header is not compared."
        echo "#"
        echo "# recorded: $(date -u +%Y-%m-%d)"
        echo "# mode: -Xint (the mandated default; the AOT tier is not an oracle)"
        echo "# image: $IMAGE"
        echo "# art-standalone: $IMAGE_VER"
        cat "$OBSERVED"
    } > "$GOLDEN"
    echo "check-golden.sh: recorded $(body "$GOLDEN" | wc -l) records to $GOLDEN"
    echo "  art_standalone $IMAGE_VER, -Xint"
    exit 0
fi

GOLDEN_LABEL='golden'
OBSERVED_LABEL='observed (-Xint, this run)'
if [[ $IMAGE_MOVED -eq 1 ]]; then
    # Name the versions in the diff labels, so the two sides say which artifact produced them.
    GOLDEN_LABEL="golden (art_standalone $GOLDEN_VER)"
    OBSERVED_LABEL="observed (-Xint, art_standalone $IMAGE_VER)"
fi

# `|| true` because `diff` exits 1 on a difference, which here is data and not an error.
DELTA="$(diff -u --label "$GOLDEN_LABEL" --label "$OBSERVED_LABEL" \
        <(body "$GOLDEN") "$OBSERVED" || true)"
RECORDS="$(body "$GOLDEN" | wc -l)"

if [[ $IMAGE_MOVED -eq 1 ]]; then
    echo "==> THE IMAGE MOVED: $GOLDEN_VER -> $IMAGE_VER"
    if [[ -z "$DELTA" ]]; then
        echo "    ...and NOT ONE ANSWER CHANGED: all $RECORDS records are identical."
        echo "FAIL: only the golden's recorded version is stale." >&2
        echo "  Nothing to decide about the answers. Refresh the header with --update." >&2
    else
        echo "    This is what changed. Some or all of it may be the new ART; decide which."
        printf '%s\n' "$DELTA"
        echo "FAIL: the image moved AND answers moved." >&2
        report_moved "$DELTA"
        echo "  Re-run verify-xint-mandate.sh (the mandate is a claim about the OLD image)," >&2
        echo "  confirm every changed record above is a change you accept, then --update." >&2
    fi
    exit 2
fi

if [[ -z "$DELTA" ]]; then
    echo "==> OK: Tier A still answers what the golden recorded ($RECORDS records, -Xint)"
    exit 0
fi

printf '%s\n' "$DELTA"
echo "FAIL: the Tier A batch no longer matches the golden." >&2
report_moved "$DELTA"
echo "  art_standalone $IMAGE_VER is unchanged, so this is a fixture edit or ART answering" >&2
echo "  differently. Decide which, then accept it with --update." >&2
exit 1
