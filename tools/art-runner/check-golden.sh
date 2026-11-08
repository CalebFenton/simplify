#!/usr/bin/env bash
# Compares the Tier A batch against the recorded golden, so a pin bump or a fixture edit cannot
# silently re-answer a settled question.
#
#     tools/art-runner/check-golden.sh             # compare; nonzero exit on any mismatch
#     tools/art-runner/check-golden.sh --update    # re-record, once a human has accepted the diff
#
# A mismatch has three causes: the image changed (the version block, checked first), a fixture
# changed (the source hashes and the diff), or ART answers differently (what remains).
# Exit status: 2 means the image is not the one recorded against, 1 means a fixture or ART answer
# moved, 0 means no drift.
#
# The header records what the answers were produced from: the art_standalone version, the dex API
# level, and the SHA-256 of every dispatcher and fixture source. `DeviceFixturesTest` checks the API
# level and the hashes without Docker, so an edited fixture cannot keep a stale answer green.
#
# The golden pins ART's message text on purpose: it detects drift in one tool, and a changed message
# is a change worth noticing. Only non-semantic churn is normalized:
#   1. ` (declaration of 'X' appears in <path>.dex)`, a verifier-diagnostic suffix naming the dex a
#      class came from, is dropped.
#   2. Any absolute `*.dex` path becomes `<DEX>`; container mount paths leak into messages such as
#      `UnsatisfiedLinkError`'s `DexPathList`.
# The `RUNTIME` line is compared: an ART version change is what this file exists to catch.
#
# Mode is always `-Xint`: this image's AOT tier is unsound (see run-batch.sh).
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
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

# Rule 1 removes the whole parenthetical including its path, so rule 2 sees only message paths.
normalize() {
    sed -e "s/ (declaration of '[^']*' appears in [^)]*)//g" \
        -e 's#/[^ "]*\.dex#<DEX>#g'
}

# The leading `#` block is provenance, not an observation, so it is not compared.
body() {
    sed -n '/^[^#]/,$p' "$1"
}

# One `# source-sha256: <hash>  <path>` line per batch input, paths relative to this directory.
source_hashes() {
    (cd "$HERE" && sha256sum dispatcher/*.smali fixtures/*.smali) | sed 's/^/# source-sha256: /'
}

# Names the records a unified diff moved. The RUNTIME line and lines a fixture printed itself (a
# `<clinit>`) carry no fixture name.
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

    # The image is not the pinned artifact, so no answer it gives is comparable.
    if [[ "$PINNED_VER" != "$IMAGE_VER" ]]; then
        echo "FAIL: the image on this host is not the pinned one, so there is nothing" >&2
        echo "  meaningful to compare. Rebuild and re-run:" >&2
        echo "    tools/art-runner/build-image.sh" >&2
        exit 2
    fi

    # The pin moved and the image was rebuilt, but the golden is older. The answers are still
    # comparable, so the batch runs and the diff is printed; the status stays 2.
    if [[ "$GOLDEN_VER" != "$IMAGE_VER" ]]; then
        IMAGE_MOVED=1
    fi
fi

OBSERVED="$(mktemp)"
RUN_ERR="$(mktemp)"
trap 'rm -f "$OBSERVED" "$RUN_ERR"' EXIT

# `ART_RUNNER_MODE=int` is set explicitly so an exported `aot` cannot select a tier that is not an
# oracle. run-batch.sh's stderr is kept for the failure report.
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
        echo "# dex-api-level: $(cat "$REPO/build/art-runner/api-level.txt")"
        source_hashes
        cat "$OBSERVED"
    } > "$GOLDEN"
    echo "check-golden.sh: recorded $(body "$GOLDEN" | wc -l) records to $GOLDEN"
    echo "  art_standalone $IMAGE_VER, -Xint"
    exit 0
fi

GOLDEN_LABEL='golden'
OBSERVED_LABEL='observed (-Xint, this run)'
if [[ $IMAGE_MOVED -eq 1 ]]; then
    GOLDEN_LABEL="golden (art_standalone $GOLDEN_VER)"
    OBSERVED_LABEL="observed (-Xint, art_standalone $IMAGE_VER)"
fi

# `|| true`: `diff` exits 1 on a difference, which here is data and not an error.
DELTA="$(diff -u --label "$GOLDEN_LABEL" --label "$OBSERVED_LABEL" \
        <(body "$GOLDEN") "$OBSERVED" || true)"
SOURCE_DELTA="$(diff -u --label 'golden sources' --label 'current sources' \
        <(grep '^# source-sha256: ' "$GOLDEN") <(source_hashes) || true)"
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

if [[ -z "$DELTA" && -z "$SOURCE_DELTA" ]]; then
    echo "==> OK: Tier A still answers what the golden recorded ($RECORDS records, -Xint)"
    exit 0
fi

if [[ -n "$SOURCE_DELTA" ]]; then
    printf '%s\n' "$SOURCE_DELTA"
    echo "Sources changed since the golden was recorded." >&2
fi
if [[ -n "$DELTA" ]]; then
    printf '%s\n' "$DELTA"
    report_moved "$DELTA"
else
    echo "  Every answer is unchanged." >&2
fi
echo "FAIL: the golden does not describe the current batch." >&2
echo "  art_standalone $IMAGE_VER is unchanged, so this is a fixture edit or ART answering" >&2
echo "  differently. Decide which, then accept it with --update." >&2
exit 1
