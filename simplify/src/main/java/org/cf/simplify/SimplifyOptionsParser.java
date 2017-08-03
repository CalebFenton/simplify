package org.cf.simplify;

import com.google.common.io.Files;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.regex.Pattern;

public class SimplifyOptionsParser {

    private static final Options options = buildOptions(new SimplifyOptions());

    static SimplifyOptions parse(String[] args) throws ParseException {
        SimplifyOptions simplifyOptions = new SimplifyOptions();
        Options opts = buildOptions(simplifyOptions);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(opts, args);

        return getSimplifyOptions(cmd);
    }

    static Options getOptions() {
        return options;
    }

    private static SimplifyOptions getSimplifyOptions(CommandLine cmd) throws ParseException {
        SimplifyOptions simplifyOpts = new SimplifyOptions();

        for (Option opt : cmd.getOptions()) {
            String val = opt.getValue();
            switch (opt.getLongOpt()) {
                case "output":
                    simplifyOpts.setOutFile(new File(val));
                    break;
                case "include-types":
                    simplifyOpts.setIncludeFilter(Pattern.compile(val));
                    break;
                case "exclude-types":
                    simplifyOpts.setExcludeFilter(Pattern.compile(val));
                    break;
                case "max-execution-time":
                    simplifyOpts.setMaxExecutionTime(Integer.parseInt(val));
                    break;
                case "max-address-visits":
                    simplifyOpts.setMaxAddressVisits(Integer.parseInt(val));
                    break;
                case "max-call-depth":
                    simplifyOpts.setMaxCallDepth(Integer.parseInt(val));
                    break;
                case "max-method-visits":
                    simplifyOpts.setMaxMethodVisits(Integer.parseInt(val));
                    break;
                case "include-support":
                    simplifyOpts.setIncludeSupportLibrary(Boolean.parseBoolean(val));
                    break;
                case "remove-weak":
                    simplifyOpts.setRemoveWeak(Boolean.parseBoolean(val));
                    break;
                case "max-passes":
                    simplifyOpts.setMaxOptimizationPasses(Integer.parseInt(val));
                    break;
                case "output-api-level":
                    simplifyOpts.setOutputAPILevel(Integer.parseInt(val));
                    break;
                case "help":
                    simplifyOpts.setIsHelp();
                    break;
                case "quiet":
                    simplifyOpts.setIsQuiet();
                    break;
                case "ignore-errors":
                    simplifyOpts.setIgnoreErrors();
                case "verbose":
                    if (val == null) {
                        simplifyOpts.setVerbosity(1);
                    } else {
                        simplifyOpts.setVerbosity(Integer.parseInt(val));
                    }
                    break;
            }
        }

        if (cmd.getArgs().length < 1) {
            throw new ParseException("Missing input file.");
        }

        String inputPath = cmd.getArgs()[0];
        simplifyOpts.setInFile(new File(inputPath));
        if (simplifyOpts.getOutFile() == null) {
            String fileName = simplifyOpts.getInFile().toString();
            String baseName = Files.getNameWithoutExtension(fileName);
            String outFileName = baseName + "_simple";

            if (simplifyOpts.isDirectory()) {
                outFileName += ".dex";
            } else {
                String ext = Files.getFileExtension(fileName);
                if (!ext.isEmpty()) {
                    outFileName += "." + ext;
                }
            }

            simplifyOpts.setOutFile(new File(outFileName));
        }

        return simplifyOpts;
    }

    private static Options buildOptions(SimplifyOptions simplifyOpts) {
        Options options = new Options();

        Option outputOption =
                Option.builder("o").longOpt("output").hasArg().argName("file").desc("Output simplified input to FILE")
                        .build();

        // Execution
        Option includeTypesOption = Option.builder("it").longOpt("include-types").hasArg().argName("pattern")
                                            .desc("Limit execution to classes and methods which include REGEX, eg: " +
                                                  "\";->targetMethod\\(\"").build();

        Option excludeTypesOption = Option.builder("et").longOpt("exclude-types").hasArg().argName("pattern")
                                            .desc("Exclude classes and methods which include REGEX, eg: " +
                                                  "\"com/android\", applied after include-types").build();

        Option maxExecutionTimeOption = Option.builder().longOpt("max-execution-time").hasArg().argName("N")
                                                .desc("Give up executing a method after N seconds, default: " +
                                                      simplifyOpts.getMaxExecutionTime()).build();

        Option maxAddressVisitsOption = Option.builder().longOpt("max-address-visits").hasArg().argName("N")
                                                .desc("Give up executing a method after visiting the same address N " +
                                                      "times, limits loops, default: " +
                                                      simplifyOpts.getMaxAddressVisits()).build();

        Option maxCallDepthOption = Option.builder().longOpt("max-call-depth").hasArg().argName("N")
                                            .desc("Do not call methods after reaching a call depth of N, limits " +
                                                  "recursion and long method chains, default: " +
                                                  simplifyOpts.getMaxCallDepth()).build();

        Option maxMethodVisitsOption = Option.builder().longOpt("max-method-visits").hasArg().argName("N")
                                               .desc("Give up executing a method after executing N instructions in " +
                                                     "that method, default: " + simplifyOpts.getMaxMethodVisits())
                                               .build();

        Option includeSupportOption = Option.builder().longOpt("include-support")
                                              .desc("Attempt to execute and optimize classes in Android support " +
                                                    "library packages, default: " +
                                                    simplifyOpts.includeSupportLibrary()).build();

        // Optimization
        Option removeWeakOption = Option.builder().longOpt("remove-weak")
                                          .desc("Remove code even if there are weak side effects, default: " +
                                                simplifyOpts.isRemoveWeak()).build();

        Option maxPassesOption = Option.builder().longOpt("max-passes").hasArg().argName("N")
                                         .desc("Do not run optimizers on a method more than N times, default: " +
                                               simplifyOpts.getMaxOptimizationPasses()).build();

        // --exclude-strategy=strat,strat2
        Option excludeStrategiesOption;

        // Graphing
        // CFG before / after, Exec graph before / after
        Option graphOption;

        // Misc
        Option outputAPILevelOption = Option.builder().longOpt("output-api-level").hasArg().argName("LEVEL")
                                              .desc("Set output DEX API compatibility to LEVEL, default: " +
                                                    simplifyOpts.getOutputAPILevel()).build();

        Option helpOption = Option.builder("h").longOpt("help").desc("Display this message").build();

        Option ignoreErrorsOption = Option.builder("ie").longOpt("ignore-errors").desc("Ignore errors while executing and optimizing methods. This may lead to unexpected behavior.").build();

        Option quietOption = Option.builder("q").longOpt("quiet").desc("Be quiet").build();

        Option verboseOption = Option.builder("v").longOpt("verbose").hasArg().optionalArg(true).argName("LEVEL")
                                       .desc("Set verbosity to LEVEL, default: " + simplifyOpts.getVerbosity()).build();

        options.addOption(outputOption);
        options.addOption(includeTypesOption);
        options.addOption(ignoreErrorsOption);
        options.addOption(excludeTypesOption);
        options.addOption(maxExecutionTimeOption);
        options.addOption(maxAddressVisitsOption);
        options.addOption(maxCallDepthOption);
        options.addOption(maxMethodVisitsOption);
        options.addOption(includeSupportOption);
        options.addOption(removeWeakOption);
        options.addOption(maxPassesOption);
        options.addOption(outputAPILevelOption);
        options.addOption(helpOption);
        options.addOption(quietOption);
        options.addOption(verboseOption);

        return options;
    }

}
