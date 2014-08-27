dx --dex --no-strict --output=bin/classes.dex bin/org/cf/demosmali
baksmali --sequential-labels --code-offsets --use-locals bin/classes.dex -o resources/