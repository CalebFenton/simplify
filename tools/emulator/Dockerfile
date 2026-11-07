# Tier B: a full Android emulator (API 36) as a long-lived container.
#
# This is the SIBLING of tools/art-runner/, not its replacement. art-runner runs ART as a
# plain Linux binary and answers opcode + libcore questions in 0.6ms per fixture. It has no
# Android framework and no `nterp` at all. This image has both. Measured 2026-08-04: 84s to
# build, 3.76GB on disk, 70s to boot. Use the cheap one by default; use this one for what the
# cheap one structurally cannot answer. See README.md for the comparison table.
#
# LICENSING, AND IT IS NOT A FORMALITY: every x86_64 system image -- `default` included --
# is gated behind `android-sdk-license`, and Android SDK Licence section 3.4 forbids
# redistributing system images. Building locally under the section 3.1 grant and keeping the
# result private is fine. NEVER `docker push` this image anywhere. There is no
# org.opencontainers.image.source label here on purpose: this artifact has no publishable
# provenance.
#
# WHAT IS PINNED, AND WHAT CANNOT BE
#
#   PINNED  base image by digest -- `ubuntu:24.04` is a moving tag.
#   PINNED  cmdline-tools by its build number (22.0 == 15859902). `sdkmanager` itself is the
#           thing that resolves every other package, so an unpinned bootstrap makes the whole
#           image unreproducible from the first layer.
#   PINNED  the system-image package string, including `default` rather than `google_apis`.
#
#   NOT PINNABLE: the *revisions* `sdkmanager` resolves for `platform-tools`, `emulator`, and
#   `system-images;android-36;default;x86_64`. Google publishes exactly one current revision
#   per package path in repository2-3.xml and serves no version-addressed URL for them, so
#   `sdkmanager "emulator"` means "whatever is current today" and there is no flag that
#   changes that. The versions actually resolved are recorded into /opt/sdk-manifest.txt at
#   build time so a later divergence is at least *visible*: diff that file. If a bit-exact
#   rebuild is ever needed, `docker save` is the only real answer -- same conclusion
#   art-runner's README reaches for its own pin.
#
# WHY `default` AND NOT `google_apis`: 844MB vs 1.90GB, and Play-services baggage is pure
# noise for dalvikvm work. `adb root` works on `default`, which `google_apis` denies.

FROM ubuntu:24.04@sha256:561618e2c15bf2397621dd04f96926663a3b5616c189cf7e38db7e82f5c538ea

ARG CMDLINE_TOOLS_BUILD=15859902
ARG SYSTEM_IMAGE="system-images;android-36;default;x86_64"
ARG AVD_NAME=simplify-api36

LABEL org.opencontainers.image.title="simplify Android emulator (Tier B)"
LABEL org.opencontainers.image.description="API 36 system image under the QEMU emulator, for framework/nterp questions art-runner cannot answer"
LABEL org.opencontainers.image.licenses="NOT-REDISTRIBUTABLE (Android SDK Licence 3.4)"
LABEL org.cf.simplify.cmdline-tools-build="${CMDLINE_TOOLS_BUILD}"
LABEL org.cf.simplify.system-image="${SYSTEM_IMAGE}"

ENV DEBIAN_FRONTEND=noninteractive \
    ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    ANDROID_AVD_HOME=/root/.android/avd \
    AVD_NAME=${AVD_NAME} \
    JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:/opt/android-sdk/emulator:${PATH}"

# THE ONE PACKAGE EVERYONE MISSES IS `libxkbfile1`. Without it `qemu-system-x86_64` dies with
# `libxkbfile.so.1: cannot open shared object file` -- under `-no-window`, on a headless box,
# with no X server anywhere. It is not optional and it is not about the GUI.
#
# The libxcb-* set is here for the same reason. Note that `ldd` on the emulator binaries
# reports DOZENS of other "not found" libraries: IGNORE THOSE. They ship inside
# emulator/lib64 and resolve through the launcher's own rpath, so chasing them with apt is
# wasted effort. libxkbfile1 is the genuine host-side gap.
#
# Java 21 (>= 17 is sdkmanager's floor; 21 is verified working end to end here).
RUN apt-get update -qq \
 && apt-get install -y -qq --no-install-recommends \
      ca-certificates \
      curl \
      unzip \
      openjdk-21-jdk-headless \
      libxkbfile1 \
      libxcb1 \
      libxcb-cursor0 \
      libxcb-icccm4 \
      libxcb-image0 \
      libxcb-keysyms1 \
      libxcb-randr0 \
      libxcb-render-util0 \
      libxcb-shape0 \
      libxcb-shm0 \
      libxcb-sync1 \
      libxcb-util1 \
      libxcb-xfixes0 \
      libxcb-xinerama0 \
      libxcb-xkb1 \
      libx11-6 \
      libxkbcommon0 \
      libxkbcommon-x11-0 \
      libgl1 \
      libglu1-mesa \
      libpulse0 \
      libnss3 \
      libasound2t64 \
      procps \
 && rm -rf /var/lib/apt/lists/*

# `sdkmanager` refuses to run non-interactively without accepted licences, and piping `yes`
# into it is racy enough to hang a build. Pre-seeding the hash files is deterministic. All
# three historical `android-sdk-license` hashes are written because which one sdkmanager
# checks depends on its own version, and an unmatched hash sends it back to the interactive
# prompt with no useful diagnostic.
RUN mkdir -p "${ANDROID_HOME}/licenses" \
 && printf '\n8933bad161af4178b1185d1a37fbf41ea5269c55\nd56f5187479451eabf01fb78af6dfcb131a6481e\n24333f8a63b6825ea9c5514f83c2829b004d1fee\n' \
      > "${ANDROID_HOME}/licenses/android-sdk-license" \
 && printf '\n84831b9409646a918e30573bab4c9c91346d8abd\n504667f4c0de7af1a06de9f4b1727b84351f2910\n' \
      > "${ANDROID_HOME}/licenses/android-sdk-preview-license" \
 && printf '\n859f317696f67ef3d7f30a50a5560e7834b43903\n' \
      > "${ANDROID_HOME}/licenses/android-sdk-arm-dbt-license"

# cmdline-tools must land at cmdline-tools/latest/ -- that exact path is what sdkmanager
# expects when it locates its own SDK root, and it is NOT what the zip unpacks to.
RUN curl -fsSL -o /tmp/cmdline-tools.zip \
      "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_BUILD}_latest.zip" \
 && mkdir -p "${ANDROID_HOME}/cmdline-tools" \
 && unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_HOME}/cmdline-tools" \
 && mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest" \
 && rm -f /tmp/cmdline-tools.zip \
 && mkdir -p /root/.android \
 && touch /root/.android/repositories.cfg \
 && sdkmanager --version

# The system image is the 844MB half of this build. `platforms;android-36` is deliberately
# NOT installed: nothing here compiles or packages an APK, and avdmanager needs only the
# system image.
RUN sdkmanager --install "platform-tools" "emulator" "${SYSTEM_IMAGE}" \
 && sdkmanager --list_installed > /opt/sdk-manifest.txt \
 && cat /opt/sdk-manifest.txt

# AVD at build time, so `start.sh` is pure orchestration and a cold start has nothing to set
# up. `echo no` declines "Do you wish to create a custom hardware profile?", which is the
# prompt that otherwise wedges a non-interactive build.
#
# The config tweaks are all about headless batch use: a data partition with room for pushed
# dexes, and no audio devices to probe.
RUN echo no | avdmanager --clear-cache create avd \
      --force --name "${AVD_NAME}" --package "${SYSTEM_IMAGE}" \
 && { \
      echo 'disk.dataPartition.size=4096M'; \
      echo 'hw.audioInput=no'; \
      echo 'hw.audioOutput=no'; \
      echo 'hw.keyboard=yes'; \
    } >> "${ANDROID_AVD_HOME}/${AVD_NAME}.avd/config.ini" \
 && avdmanager list avd

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh

# The console/adb ports are exposed for documentation value only; `docker exec` is the
# supported way in, because the in-container adb already owns the device.
EXPOSE 5554 5555

WORKDIR /root
ENTRYPOINT ["/opt/entrypoint.sh"]
