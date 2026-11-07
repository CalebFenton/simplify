#!/usr/bin/env bash
# Builds the Tier A art-runner image and PROVES it is usable before returning.
#
#     tools/art-runner/build-image.sh
#
# Measured from scratch: 12.2s build, 373MB image, 0.65s container start. Neither KVM nor
# `--privileged` nor any added capability is involved, so this runs anywhere Docker does.
#
# The three post-build checks exist because each corresponds to a way the build has
# actually been observed to "succeed" while producing something useless:
#   - `-showversion` proves dalvikvm links (a missing shared library fails here).
#   - the baked boot image proves the warmup layer worked; without it every later run pays
#     ~5s of dex2oat and the harness looks mysteriously slow.
#   - one real fixture batch proves the whole chain, because the first two can both pass on
#     an image that cannot execute a class file.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGE="${ART_RUNNER_IMAGE:-simplify-art-runner:pinned}"

echo "==> building $IMAGE"
docker build -t "$IMAGE" -f "$HERE/Dockerfile" "$HERE"

echo "==> checking dalvikvm links and reports a version"
VERSION="$(docker run --rm "$IMAGE" -showversion 2>&1 | tail -1)"
echo "    $VERSION"
case "$VERSION" in
    *"ART version"*) ;;
    *) echo "build-image.sh: dalvikvm did not report an ART version" >&2; exit 1 ;;
esac

echo "==> checking the boot image is baked into the layer (not rebuilt per run)"
docker run --rm --entrypoint sh "$IMAGE" -c 'ls /root/.cache/art/x86_64/*boot.art' >/dev/null

echo "==> running one real fixture batch end to end"
ART_RUNNER_IMAGE="$IMAGE" "$HERE/run-batch.sh" Fx_DivOverflow | sed 's/^/    /'

echo "==> OK: $IMAGE is a working Tier A oracle"
