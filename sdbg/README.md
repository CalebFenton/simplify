# Smali Debugger (sdbg)

Sdbg is a Smali debugger powered by SmaliVM. The command line interface is loosely based on [gdb](https://sourceware.org/gdb/current/onlinedocs/gdb/). All of the commands are documented within the app.

[![demo](https://asciinema.org/a/327115.svg)](https://asciinema.org/a/327115)

Right now, it's only an experiment, so if you have feature suggestions, feel free to create an issue or pull request.

SDBG drives one Kotlin `ExecutionSession`. Unknown inputs can fork execution into multiple paths.
VM limits bound execution; a finished session can report partial analysis or guest exceptions.

## Building

From the root of this repository, with JDK 21 selected through `JAVA_HOME`:

```bash
./gradlew :sdbg:fatJar
```

The standalone jar is `sdbg/build/libs/sdbg.jar`. It includes the module opening required by host reflection.

## Usage

```plaintext
Usage: <main class> [-hVv] input-path method-signature
Smali DeBuGger (SDBG)
      input-path         Input directory containing Smali
      method-signature   Method signature to execute
  -h, --help             Show this help message and exit.
  -v, --verbose          Specify multiple -v options to increase verbosity.
                         For example, `-v -v -v` or `-vvv`
  -V, --version          Print version information and exit.
```

```plaintext
Commands:
  help         Display help information about the specified command.
  cls, clear   Clears the screen
  list, l      List source code
  where, w     Shows current instruction index and which function you are in
  step, s      Step to next line of code including stepping into methods
  next, n      Execute next line of code without entering methods
  info, i      List information about target
  print, p     Print value stored in variable or static field
  break, b     Suspend program at specified function, instruction index, or
                 index offset
  continue, c  Continue executing until next break point
```

Start the included example:

```bash
java -jar sdbg/build/libs/sdbg.jar sdbg/resources 'LTest;->addNumbers()V'
```

Then enter:

```text
list
break 4
continue
print v0
step
where
print p0
next
print p0
```

The breakpoint stops before the call to `addParameters`. Its arguments are 8 and 3;
after the addition, `p0` contains 11. `next` steps over calls but respects breakpoints
inside them. `step` enters guest or framework method bodies when the VM executes them.

Listing indexes count executable instructions, not source lines or DEX code units.
`b` marks a breakpoint and `>` marks the next instruction. For example, `0:b>` marks
both at index zero. `list 0,2` selects an inclusive instruction range; labels, debug
directives, and unnumbered payload data remain as context. `list LTest;` lists a class.
Listings also work inside framework callees.

Targets use dex descriptor syntax: a class is `LTest;`, a method is `addParameters(II)I` in the
current class or `LTest;->addParameters(II)I`, and a static field is `LTest;->name:I`. A malformed
or unknown target prints `invalid target;` with the reason and changes nothing.

The first application listing serializes the immutable application snapshot once and caches
the decoded classes. Large applications can therefore make the first listing slower.
Framework listings use the catalog's cached immutable class definitions. Listing does not advance execution.

TAB completes commands. Ctrl-C cancels the current input; Ctrl-D closes the terminal and exits.
