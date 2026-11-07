#!/usr/bin/env bash
# Stops and removes the emulator container.
#
#     tools/emulator/stop.sh            # stop and remove (the next start is a clean cold boot)
#     tools/emulator/stop.sh --keep     # stop only; `start.sh` will restart THIS container
#
# THINK BEFORE YOU RUN THIS. The intended workflow is to leave the container running for days;
# stopping costs a 67-70s cold boot to undo, and removing also discards everything pushed into
# /data/local/tmp. Neither is a problem -- it is just work you do not need to do.
#
# THE TWO MODES ARE NOT THE SAME DEVICE, and this is the part worth knowing:
#
#   default (stop + remove)  next start is a GENUINE COLD BOOT, measured 67-70s. Removing the
#                            container discards the writable layer, and with it the emulator's
#                            `default_boot` quickboot snapshot -- so there is nothing stale left
#                            to restore from. This is the trustworthy option.
#   --keep  (stop only)      next start is a QUICKBOOT SNAPSHOT RESTORE, measured 6s. Fast, and
#                            fine for iterating, but it is a resumed device rather than a fresh
#                            one. If a measurement looks strange after a --keep restart, redo it
#                            after a full stop+start before believing it.
#
# The emulator attempts that snapshot restore on every start whether or not you asked for it (it
# logs `Loading snapshot 'default_boot'...` each time), which is exactly why the DEFAULT here is
# the destructive one: a harness should not silently inherit yesterday's device state.
#
# The image is never touched. Removing 7GB of Android SDK should be a deliberate act:
#     docker rmi simplify-emulator:api36
set -euo pipefail

CONTAINER="${EMULATOR_CONTAINER:-simplify-emulator}"
KEEP=0
[[ "${1:-}" == "--keep" ]] && KEEP=1

# `docker ps --filter`, not `docker inspect`: see the note in start.sh -- `docker inspect -f` on
# a missing container writes a bare newline to stdout and exits 1 on Docker 29.7.1, which would
# make this script try to stop a container that is not there.
STATE="$(docker ps -a --filter "name=^${CONTAINER}$" --format '{{.State}}')"
if [[ -z "$STATE" ]]; then
    echo "stop.sh: no container named '$CONTAINER'; nothing to do"
    exit 0
fi

if [[ "$STATE" == "running" ]]; then
    # -t 30 rather than the 10s default: the `emulator` launcher forks qemu instead of exec'ing
    # it, so SIGTERM has two processes to travel through before the VM actually winds down. A
    # short timeout just means SIGKILL, which for a disposable VM is survivable but leaves a
    # dirty data partition behind for any `--keep` restart.
    echo "stop.sh: stopping $CONTAINER"
    docker stop -t 30 "$CONTAINER" >/dev/null
fi

if [[ $KEEP -eq 1 ]]; then
    echo "stop.sh: stopped, container kept. Restart with: tools/emulator/start.sh"
    exit 0
fi

docker rm "$CONTAINER" >/dev/null
echo "stop.sh: stopped and removed $CONTAINER (image left in place)"
