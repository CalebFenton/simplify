#!/usr/bin/env bash
# Drops you inside the emulator container, for poking around by hand.
#
#     tools/emulator/shell.sh                 # a bash shell IN THE CONTAINER (adb on PATH)
#     tools/emulator/shell.sh --device        # a shell ON THE ANDROID DEVICE (adb -e shell)
#     tools/emulator/shell.sh adb devices     # run one command in the container and exit
#
# Two levels are worth keeping straight, because half of "adb isn't working" is confusion
# between them:
#   the CONTAINER shell is Ubuntu, and is where adb / emulator / sdkmanager live;
#   the DEVICE shell is Android, and is where dalvikvm / getprop / logcat live.
#
# From the container shell, useful next steps:
#     adb -e shell                                    # into the device
#     adb -e logcat -d | tail -50                     # what ART just said
#     adb -e shell ls -l /apex/com.android.art/bin/   # where dalvikvm actually is on API 36
#     adb -e root                                     # works on the `default` image
set -euo pipefail

CONTAINER="${EMULATOR_CONTAINER:-simplify-emulator}"

# `docker ps --filter`, not `docker inspect`: see the note in start.sh -- `docker inspect -f` on
# a missing container writes a bare newline to stdout and exits 1 on Docker 29.7.1.
if [[ "$(docker ps -a --filter "name=^${CONTAINER}$" --format '{{.State}}')" != "running" ]]; then
    echo "shell.sh: container '$CONTAINER' is not running. Start it first:" >&2
    echo "    tools/emulator/start.sh" >&2
    exit 1
fi

# -t only when there is a real terminal, so this stays usable from a script or an agent.
TTY_FLAGS=(-i)
[[ -t 0 && -t 1 ]] && TTY_FLAGS=(-i -t)

if [[ "${1:-}" == "--device" ]]; then
    shift
    exec docker exec "${TTY_FLAGS[@]}" "$CONTAINER" adb -e shell "$@"
fi

if [[ $# -gt 0 ]]; then
    exec docker exec "${TTY_FLAGS[@]}" "$CONTAINER" "$@"
fi

exec docker exec "${TTY_FLAGS[@]}" "$CONTAINER" bash
