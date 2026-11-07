#!/usr/bin/env bash
echo "Compiling Java classes to Dalvik executable"
dx --dex --no-strict --output=build/classes.dex build/classes/java/main/org/cf/demosmali

echo "Disassembling Dalvik executable"
baksmali disassemble --sequential-labels --code-offsets --use-locals build/classes.dex -o resources/
