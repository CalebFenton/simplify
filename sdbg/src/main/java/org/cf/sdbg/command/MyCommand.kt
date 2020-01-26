package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.ParentCommand
import java.util.concurrent.TimeUnit

/**
 * A command with some options to demonstrate completion.
 */
@CommandLine.Command(name = "cmd", mixinStandardHelpOptions = true, version = ["1.0"], description = ["Command with some options to demonstrate TAB-completion" +
        " (note that enum values also get completed)"], subcommands = [CommandLine.HelpCommand::class])
class MyCommand : Runnable {
    @CommandLine.Option(names = ["-v", "--verbose"], description = ["Specify multiple -v options to increase verbosity.", "For example, `-v -v -v` or `-vvv`"])
    private val verbosity = booleanArrayOf()
    @ArgGroup(exclusive = false)
    private val myDuration = MyDuration()

    internal class MyDuration {
        @CommandLine.Option(names = ["-d", "--duration"], description = ["The duration quantity."], required = true)
        val amount = 0
        @CommandLine.Option(names = ["-u", "--timeUnit"], description = ["The duration time unit."], required = true)
        val unit: TimeUnit? = null
    }

    @ParentCommand
    var parent: CliCommands? = null

    override fun run() {
        if (verbosity.size > 0) {
            parent!!.out!!.printf("Hi there. You asked for %d %s.%n",
                    myDuration.amount, myDuration.unit)
        } else {
            parent!!.out!!.println("hi!")
        }
    }
}
