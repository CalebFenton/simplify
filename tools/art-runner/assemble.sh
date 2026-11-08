#!/usr/bin/env bash
# Assembles the ART runner's dispatcher and fixtures into two dex files.
#
# Two dexes rather than one, on purpose: the dispatcher never changes and the fixtures
# change constantly, and `dalvikvm` accepts a multi-entry classpath
# (`-cp dispatcher.dex:fixtures.dex`, verified), so the dispatcher is assembled once and
# only the fixture dex is rebuilt.
#
# API LEVEL ALIGNMENT IS LOAD-BEARING, which is why it is read out of the engine rather
# than written down here. `SmaliParser.DEX_API_LEVEL` is what `:smalivm` parses its own
# fixtures at; assembling the device fixtures at any other level means the two sides are
# not diffing the same bytes. Google's smali 3.0.10 must receive the same explicit level;
# relying on the assembler's default is not an option.
#
# `--jobs 1` for the same reason: AOSP's build notes that smali's output is not
# deterministic across worker counts, and these dexes feed golden-file comparisons.
#
# Usage: assemble.sh [--force]
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
OUT="$REPO/build/art-runner"

FORCE=0
[[ "${1:-}" == "--force" ]] && FORCE=1

API_SOURCE="$REPO/smalivm/src/main/java/org/cf/smalivm/dex/SmaliParser.kt"
API_LEVEL="$(sed -n 's/.*DEX_API_LEVEL *= *\([0-9]\+\).*/\1/p' "$API_SOURCE" | head -1)"
if [[ ! "$API_LEVEL" =~ ^[0-9]+$ ]]; then
    echo "assemble.sh: could not read DEX_API_LEVEL from $API_SOURCE" >&2
    exit 1
fi

mkdir -p "$OUT"

# Let Gradle resolve the current assembler every time. A separate classpath cache can
# retain removed dependencies after an upgrade; Gradle already caches the artifacts.
echo "assemble.sh: resolving the smali classpath via Gradle" >&2
if ! SMALI_CP="$("$REPO/gradlew" -q -p "$REPO" :smalivm:printSmaliAssemblerClasspath \
        | grep -F 'smali-3.0.10.jar' | tail -1)"; then
    echo "assemble.sh: could not resolve a classpath containing smali-3.0.10.jar" >&2
    exit 1
fi

assemble() {
    local out_dex="$1"; shift
    java -cp "$SMALI_CP" com.android.tools.smali.smali.Main assemble \
        --api "$API_LEVEL" --jobs 1 --output "$out_dex" "$@"
}

DISPATCHER_DEX="$OUT/art-dispatcher.dex"
FIXTURES_DEX="$OUT/art-fixtures.dex"

# THE API LEVEL IS A STALENESS INPUT, not just a flag passed at assemble time. Both dexes below
# otherwise rebuild only on mtime (a dispatcher/fixture .smali newer than the dex), and a bare
# DEX_API_LEVEL bump touches no .smali file -- so without this the dexes would keep their old level
# while `api-level.txt`, written unconditionally at the end, advertised the new one. The engines
# parse their own fixtures at that same constant, so a silent level skew means the two sides are no
# longer diffing the same bytes -- the whole reason the level is read from source rather than pinned
# here. `|| true` because a first run has no stamp yet and `set -e` would abort on cat's failure;
# an empty value is correctly unequal to any real level and forces the (already inevitable) first build.
BUILT_API_LEVEL="$(cat "$OUT/api-level.txt" 2>/dev/null || true)"

# A change to this assembly recipe (including the assembler version) also invalidates both dexes.
if [[ $FORCE -eq 1 || ! -f "$DISPATCHER_DEX" || "$HERE/assemble.sh" -nt "$DISPATCHER_DEX" || "$HERE/dispatcher/RunFixtures.smali" -nt "$DISPATCHER_DEX" || "$BUILT_API_LEVEL" != "$API_LEVEL" ]]; then
    assemble "$DISPATCHER_DEX" "$HERE/dispatcher"
    echo "assemble.sh: dispatcher -> $DISPATCHER_DEX (api $API_LEVEL)" >&2
fi

STALE=0
if [[ $FORCE -eq 1 || ! -f "$FIXTURES_DEX" || "$HERE/assemble.sh" -nt "$FIXTURES_DEX" || "$BUILT_API_LEVEL" != "$API_LEVEL" ]]; then
    STALE=1
else
    for f in "$HERE"/fixtures/*.smali; do
        [[ "$f" -nt "$FIXTURES_DEX" ]] && STALE=1
    done
fi
if [[ $STALE -eq 1 ]]; then
    assemble "$FIXTURES_DEX" "$HERE/fixtures"
    echo "assemble.sh: fixtures -> $FIXTURES_DEX (api $API_LEVEL)" >&2
fi

# A level change rebuilt both dexes above, so the stamp is the level the dexes on disk were assembled at.
echo "$API_LEVEL" > "$OUT/api-level.txt"
