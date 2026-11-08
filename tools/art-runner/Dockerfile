# The Tier A ART runner: real AOSP ART (dalvikvm) as a native Linux binary.
#
# No emulator, no KVM, no --privileged, no added capabilities, no Android SDK.
# Upstream: https://gitlab.com/android_translation_layer/art_standalone (Apache-2.0).
#
# EVERYTHING HERE IS PINNED TWICE, AND THAT IS NOT PARANOIA.
#
#   1. The base image is pinned BY DIGEST. `alpine:edge` is a moving tag.
#   2. The package is pinned BY VERSION, because `art_standalone` exists ONLY in
#      Alpine `edge/testing`. Measured 2026-08-04: v3.21/v3.22/v3.23 have no
#      `testing` repository at all (HTTP 404 on their APKINDEX), and `edge/testing`
#      drops old versions as it advances. An unpinned build is a build that stops
#      reproducing without warning.
#
# If `apk add` starts failing because edge/testing rotated the version away, the
# recovery path is in README.md ("When the pin breaks"): the .apk is directly
# fetchable, and a `docker save` tarball is the durable copy.

FROM alpine@sha256:9a341ff2287c54b86425cbee0141114d811ae69d88a36019087be6d896cef241

# alpine-minirootfs-20260127-x86_64; `alpine:edge` as of 2026-08-04.
ARG ART_VER=0_git20251009-r2

LABEL org.opencontainers.image.title="simplify ART runner (Tier A)"
LABEL org.opencontainers.image.description="AOSP ART as a host binary, for measuring Dalvik semantics"
LABEL org.opencontainers.image.source="https://gitlab.com/android_translation_layer/art_standalone"
LABEL org.opencontainers.image.licenses="Apache-2.0 AND GPL-3.0-only"
LABEL org.cf.simplify.art-standalone-version="${ART_VER}"

RUN apk add --no-cache \
      --repository=https://dl-cdn.alpinelinux.org/alpine/edge/testing \
      "art_standalone=${ART_VER}" \
 && dalvikvm -showversion 2>&1 | tail -1

# ANDROID_DATA must exist and be writable: ART puts its dalvik-cache under it.
# ANDROID_LOG_TAGS silences the runtime's own logcat chatter so stdout is only
# the program's output, which is what the golden files compare.
ENV ANDROID_DATA=/data \
    HOME=/root \
    ANDROID_LOG_TAGS="*:s"

RUN mkdir -p /data /work

# Bake boot.art/boot.oat into the image layer. Without this EVERY container start
# pays ~5s of dex2oat over the boot classpath; with it, measured start is 0.65s.
# `|| true` because the warmup program's own exit status is meaningless here (see
# invariant 3 in run-batch.sh) -- the `test -f` on the next line is the real gate.
COPY warmup/warmup_boot_image.dex /opt/warmup_boot_image.dex
RUN dalvikvm -cp /opt/warmup_boot_image.dex Warm >/dev/null 2>&1 || true; \
    test -f /root/.cache/art/x86_64/*boot.art && echo "BOOT IMAGE BAKED OK"

WORKDIR /work
ENTRYPOINT ["dalvikvm"]
