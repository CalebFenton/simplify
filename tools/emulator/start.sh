#!/usr/bin/env bash
# Starts the emulator container and does not return until Android has actually finished
# booting -- or until it can say precisely why it did not.
#
#     tools/emulator/start.sh
#
# IDEMPOTENT. If a booted container is already up this prints one line and exits 0, so it is
# safe to put in front of anything. That matters because the intended usage is "start it once
# in the morning and leave it running": COLD BOOT IS EXPECTED AND FINE HERE. It is ~75s, it is
# not a regression, and there is deliberately no snapshot in the default path -- see README.md.
#
# Environment:
#     EMULATOR_IMAGE            image tag       (default simplify-emulator:api36)
#     EMULATOR_CONTAINER        container name  (default simplify-emulator)
#     EMULATOR_BOOT_TIMEOUT     seconds         (default 420)
#     EMULATOR_EXTRA_ARGS       extra emulator flags, e.g. '-read-only' or '-verbose'
#
# BOOT GATING IS THE SINGLE MOST COMMON WAY SCRIPTS LIKE THIS BREAK. Three measured rules:
#
#   1. POLL `getprop sys.boot_completed` UNTIL IT IS EXACTLY `1`. That is the gate.
#
#   2. `adb wait-for-device` IS NOT A BOOT GATE. Measured: it returns ~24 SECONDS before the
#      system is usable, because it only waits for the adb transport. It is used below purely
#      as a cheap way to avoid hammering getprop while the device is still absent, never as
#      the gate itself.
#
#   3. DO NOT GATE ON `init.svc.bootanim`. The ATD-style images have no bootanim service at
#      all, so `getprop init.svc.bootanim` returns empty forever and a loop waiting for it to
#      become `stopped` hangs until the timeout. This is a real trap: it is the single most
#      commonly copy-pasted emulator boot gate on the internet.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGE="${EMULATOR_IMAGE:-simplify-emulator:api36}"
CONTAINER="${EMULATOR_CONTAINER:-simplify-emulator}"
TIMEOUT="${EMULATOR_BOOT_TIMEOUT:-420}"

# `adb -e` selects "the one emulator", which is right here and stays right even if the console
# port moves. The tr strips adb's CRLF: `getprop` output is line-ended by the device's pty, so
# a naive string compare against "1" fails on the invisible \r. This has bitten every adb
# script ever written.
boot_state() {
    docker exec "$CONTAINER" adb -e shell getprop sys.boot_completed 2>/dev/null \
        | tr -d '\r\n' || true
}

# `docker ps --filter`, NOT `docker inspect`. Measured on Docker 29.7.1: `docker inspect -f
# '{{.State.Status}}' missing-container` prints a BARE NEWLINE TO STDOUT and exits 1, so the
# obvious `$(docker inspect ... 2>/dev/null || echo absent)` evaluates to $'\nabsent' and
# matches neither arm of a case statement. This cost a debugging round. `docker ps --filter`
# prints nothing and exits 0 for an absent container, which is what a probe should do.
# The anchors in `name=^...$` matter: the filter is a substring regex by default, so an
# unanchored `simplify-emulator` would also match `simplify-emulator-2`.
container_state() {
    local state
    state="$(docker ps -a --filter "name=^${CONTAINER}$" --format '{{.State}}' 2>/dev/null)"
    echo "${state:-absent}"
}

die_with_logs() {
    echo "start.sh: $1" >&2
    echo "  container state: $(container_state)" >&2
    echo "  last 30 lines of emulator output:" >&2
    docker logs --tail 30 "$CONTAINER" 2>&1 | sed 's/^/    /' >&2 || true
    echo "  full log: docker logs $CONTAINER" >&2
    exit 1
}

if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
    echo "start.sh: image '$IMAGE' not found. Build it first:" >&2
    echo "    tools/emulator/build-image.sh" >&2
    exit 1
fi

case "$(container_state)" in
    running)
        if [[ "$(boot_state)" == "1" ]]; then
            echo "start.sh: $CONTAINER is already up and booted (sys.boot_completed=1)"
            exit 0
        fi
        echo "start.sh: $CONTAINER is running but not booted yet; waiting"
        ;;
    absent)
        # --device /dev/kvm rather than --privileged: the emulator needs exactly one device
        # node and nothing else. Verified working under nested virt (host `hypervisor` flag
        # set), so a VM host is not a blocker.
        #
        # --init because the `emulator` launcher forks qemu-system-x86_64 rather than exec'ing
        # it, so PID 1 would otherwise accumulate zombies over a long-lived session -- and
        # long-lived is the whole point of this container.
        echo "start.sh: creating $CONTAINER from $IMAGE"
        docker run -d \
            --name "$CONTAINER" \
            --device /dev/kvm \
            --init \
            -e "EMULATOR_EXTRA_ARGS=${EMULATOR_EXTRA_ARGS:-}" \
            "$IMAGE" >/dev/null
        ;;
    *)
        echo "start.sh: starting existing container $CONTAINER"
        docker start "$CONTAINER" >/dev/null
        ;;
esac

# Timed from here -- i.e. from the container existing -- because "how long until I can use it"
# is the only number anyone cares about.
START="$SECONDS"

echo "start.sh: waiting for the adb transport (NOT the boot gate -- see rule 2)"
docker exec "$CONTAINER" timeout 120 adb wait-for-device >/dev/null 2>&1 || true
TRANSPORT_AT="$((SECONDS - START))"

# Both numbers are printed, and the gap between them IS the evidence for rule 2. Anyone who
# "simplifies" this script down to `adb wait-for-device` gets to see, in their own logs, exactly
# how many seconds of not-yet-booted device that would have handed them.
echo "start.sh: adb transport up at ${TRANSPORT_AT}s; now polling sys.boot_completed"
while :; do
    if [[ "$(container_state)" != "running" ]]; then
        die_with_logs "the container exited while booting"
    fi
    if [[ "$(boot_state)" == "1" ]]; then
        BOOTED_AT="$((SECONDS - START))"
        echo "start.sh: sys.boot_completed=1 at ${BOOTED_AT}s"
        echo "start.sh: => adb wait-for-device would have returned $((BOOTED_AT - TRANSPORT_AT))s TOO EARLY"
        break
    fi
    if (( SECONDS - START >= TIMEOUT )); then
        die_with_logs "sys.boot_completed never reached 1 within ${TIMEOUT}s"
    fi
    sleep 2
done

# Provenance, so a captured session is self-describing. `ro.build.version.sdk` is the number
# that decides whether an answer from this tool is about modern ART or not.
SDK="$(docker exec "$CONTAINER" adb -e shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r\n')"
REL="$(docker exec "$CONTAINER" adb -e shell getprop ro.build.version.release 2>/dev/null | tr -d '\r\n')"
echo "start.sh: OK -- API $SDK (Android $REL), container '$CONTAINER'"
echo "    run fixtures: tools/emulator/run-dex.sh"
echo "    poke around:  tools/emulator/shell.sh"
