#!/usr/bin/env bash
# Builds the Tier B emulator image and PROVES it is usable before returning.
#
#     tools/emulator/build-image.sh
#
# Measured from scratch on 2026-08-04: 84s build, 3.76GB image, 70s cold boot. The 844MB system
# image and the 182MB cmdline-tools zip are the bulk of the download, so a slower link dominates
# that 84s -- budget minutes, not seconds, the first time.
#
# Unlike tools/art-runner/build-image.sh this one needs /dev/kvm -- but only for the post-build
# checks, not for `docker build` itself.
#
# THE NETWORK LADDER, AND WHY IT EXISTS
#
# An apt-based build here was once observed to stall for ~13 MINUTES inside buildkit's network
# namespace, parked on /usr/lib/apt/methods/http with zero progress, while the host had full
# connectivity. Running apt with host networking fixed it. Re-measured 2026-08-04 on this box:
# ALL THREE incantations completed apt in 8-11s, so that stall is INTERMITTENT rather than
# structural -- do not conclude from a fast build that it cannot happen to you.
#
# So this script tries them in order and says which one won:
#   1. docker build --network=host   (sidesteps the buildkit netns entirely; needs the
#                                     network.host entitlement, which some daemons refuse)
#   2. docker build                  (plain buildkit; measured fine today)
#   3. DOCKER_BUILDKIT=0 docker build (classic builder; verified still present in Docker 29.7)
#
# The three post-build checks mirror art-runner's, and each one corresponds to a way this
# build has actually been seen to "succeed" while producing something useless:
#   - the SDK manifest proves sdkmanager resolved all three packages (a licence that failed to
#     pre-seed makes sdkmanager skip a package and exit 0).
#   - the AVD listing proves avdmanager did not silently decline at its hardware-profile
#     prompt.
#   - a real boot + a real dalvikvm run proves the whole chain, because the first two can both
#     pass on an image whose emulator cannot start.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGE="${EMULATOR_IMAGE:-simplify-emulator:api36}"

BUILD_LOG="$(mktemp -t simplify-emulator-build-XXXXXX.log)"
WON=""

try_build() {
    local label="$1"; shift
    echo "==> trying: $label"
    if "$@" -t "$IMAGE" -f "$HERE/Dockerfile" "$HERE" 2>&1 | tee "$BUILD_LOG"; then
        WON="$label"
        return 0
    fi
    # An entitlement refusal or a netns failure is worth falling back on. A compile-level
    # failure inside the image is not, and re-running it three times just wastes minutes.
    if grep -qiE 'entitlement|network mode|not supported by buildkit|unknown flag' "$BUILD_LOG"; then
        echo "    refused by the daemon; falling back" >&2
        return 1
    fi
    echo >&2
    echo "build-image.sh: the build FAILED for a reason unrelated to networking, so the" >&2
    echo "  remaining incantations are not attempted -- they would fail identically." >&2
    echo "  full log: $BUILD_LOG" >&2
    exit 1
}

if ! try_build "docker build --network=host" docker build --network=host; then
    if ! try_build "docker build (plain buildkit)" docker build; then
        try_build "DOCKER_BUILDKIT=0 docker build" env DOCKER_BUILDKIT=0 docker build
    fi
fi
rm -f "$BUILD_LOG"

# Printed prominently because it is the answer to "how do I rebuild this by hand", and because
# WHICH one won is the diagnostic if you ever hit the 13-minute apt stall described above.
echo "==> BUILT WITH: $WON"

echo "==> checking sdkmanager resolved all three packages"
MANIFEST="$(docker run --rm --entrypoint cat "$IMAGE" /opt/sdk-manifest.txt)"
echo "$MANIFEST" | sed 's/^/    /'
for pkg in "platform-tools" "emulator" "system-images;android-36;default;x86_64"; do
    case "$MANIFEST" in
        *"$pkg"*) ;;
        *)
            echo "build-image.sh: '$pkg' is missing from the SDK manifest." >&2
            echo "  sdkmanager exits 0 when a licence check silently skips a package, so this" >&2
            echo "  check is the only thing standing between you and an SDK-less image." >&2
            exit 1
            ;;
    esac
done

echo "==> checking the AVD was created at build time"
AVDS="$(docker run --rm --entrypoint avdmanager "$IMAGE" list avd 2>/dev/null)"
case "$AVDS" in
    *simplify-api36*) echo "    simplify-api36 present" ;;
    *)
        echo "build-image.sh: no simplify-api36 AVD in the image." >&2
        echo "  avdmanager prompts for a custom hardware profile and answering it wrong" >&2
        echo "  produces exactly this: a successful build with no AVD. See the Dockerfile." >&2
        exit 1
        ;;
esac

echo "==> booting it and running one real dalvikvm invocation end to end"
if [[ ! -r /dev/kvm || ! -w /dev/kvm ]]; then
    echo "build-image.sh: /dev/kvm is not read+write for $(id -un)." >&2
    echo "  The image is built, but it cannot be verified -- and an unverified emulator image" >&2
    echo "  is exactly what these checks exist to refuse. Add yourself to the 'kvm' group." >&2
    exit 1
fi
EMULATOR_IMAGE="$IMAGE" "$HERE/start.sh"
EMULATOR_IMAGE="$IMAGE" "$HERE/run-dex.sh" | sed 's/^/    /'

echo "==> OK: $IMAGE is a working Tier B oracle, and a booted container is left running"
echo "    shell in:  tools/emulator/shell.sh"
echo "    tear down: tools/emulator/stop.sh"
