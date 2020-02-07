# Smali Debugger (sdbg)

Sdbg is a Smali debugger powered by SmaliVM. The command line interface is loosely based on [gdb](https://sourceware.org/gdb/current/onlinedocs/gdb/). All of the commands are documented within the app.

Right now, it's only an experiment, so if you have feature suggestions, feel free to create an issue or pull request.

Since sdbg is powered by SmaliVM, it has all of its strengths and weaknesses. It can handle unknown values and parameters by taking every execution path, which is cool. But it's also possible to get stuck in infinite loops and debugging multiple execution paths is tricky.

## Building

From the root of this repository:

```bash
./gradlew :sdbg:fatjar
```

The full jar will be in `sdbg/build/libs/`.

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
  help         Displays help information about the specified command
  cls, clear   Clears the screen
  list, l      List source code
  where, w     Shows current instruction index and which function you are in
  step, s      Step to next line of code including stepping into methods
  next, n      Execute next line of code without entering methods
  info, i      List information about target
  print, p     Print value stored in variable or field
  break, b     Suspend program at specified function, instruction index, or
                 index offset
  continue, c  Continue executing until next break point
```
