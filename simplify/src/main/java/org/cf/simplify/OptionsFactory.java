package org.cf.simplify;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class OptionsFactory {

    public Options build(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e);
            parser.printUsage(System.out);
            System.exit(-1);
        }
        if (opts.isHelp()) {
            parser.printUsage(System.out);
            System.exit(0);
        }

        return opts;
    }

}
