#!/usr/bin/env bash
# PID 1 of the emulator container: brings up adb, then becomes the emulator.
#
# There is no boot-waiting here on purpose. Gating belongs in `start.sh`, where a failure can
# be reported to a human with a real diagnostic; an entrypoint that polled its own boot would
# only be able to exit, which looks identical to a crash.
#
# EVERY FLAG BELOW WAS MEASURED, and two of them are the difference between working and
# silently-wrong:
#
#   -gpu swiftshader   NOT `-gpu off`. `off` does not error -- it selects *guest* rendering,
#                      which modern system images do not implement, and the emulator then
#                      quietly falls back to lavapipe. `swiftshader_indirect` is the
#                      deprecated spelling as of emulator 36.4.9; use plain `swiftshader`.
#   -accel on          NOT `auto`. With `auto` a missing/unreadable /dev/kvm degrades to TCG,
#                      which is roughly 20x slower and turns a 75s boot into a mystery. `on`
#                      fails loudly instead, which is what you want in a harness.
#   -no-window         headless; still needs libxkbfile1 on the host side (see Dockerfile).
#   -no-boot-anim      shaves boot work. Note this does NOT mean "no bootanim service to
#                      poll" -- see start.sh on why polling bootanim is wrong regardless.
#   -no-metrics        no telemetry to Google.
#
# `-qemu-verbose` IS NOT A VALID FLAG. It was tried; it cost a restart. Use `-verbose`.
set -euo pipefail

: "${AVD_NAME:=simplify-api36}"

# Word-split intentionally: this is an env-var passthrough for ad-hoc flags such as
# `-read-only` (which lets a second emulator share this AVD) or `-verbose`.
# shellcheck disable=SC2206
EXTRA_ARGS=(${EMULATOR_EXTRA_ARGS:-})

if [[ ! -d "${ANDROID_AVD_HOME}/${AVD_NAME}.avd" ]]; then
    echo "entrypoint: no AVD named '${AVD_NAME}' in ${ANDROID_AVD_HOME}" >&2
    echo "entrypoint: available:" >&2
    avdmanager list avd >&2 || true
    exit 1
fi

if [[ ! -r /dev/kvm || ! -w /dev/kvm ]]; then
    echo "entrypoint: /dev/kvm is not read+write inside the container." >&2
    echo "entrypoint: the container needs '--device /dev/kvm' (start.sh passes it)." >&2
    echo "entrypoint: -accel on will refuse to start, which is deliberate: TCG is ~20x" >&2
    echo "entrypoint: slower and would look like a hang rather than a misconfiguration." >&2
    exit 1
fi

# Start the adb server before the emulator so it is already polling localhost 5555-5585 when
# the console port appears. Ordering is not strictly required -- adb rescans -- but this
# removes a first-poll race from start.sh's gate.
adb start-server >/dev/null 2>&1 || true

echo "entrypoint: starting emulator avd=${AVD_NAME} extra='${EMULATOR_EXTRA_ARGS:-}'"
exec emulator -avd "${AVD_NAME}" \
    -no-window \
    -no-audio \
    -no-boot-anim \
    -gpu swiftshader \
    -accel on \
    -no-metrics \
    "${EXTRA_ARGS[@]}"
