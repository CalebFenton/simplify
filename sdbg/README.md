# Smali Debugger (sdbg)

Sdbg is a Smali debugger powered by SmaliVM. The command line interface is loosely based on [gdb](https://sourceware.org/gdb/current/onlinedocs/gdb/). All of the commands are documented within the app.

Right now, it's only an experiment, so if you have feature suggestions, feel free to create an issue or pull request.

Since sdbg is powered by SmaliVM, it has all of its strengths and weaknesses. It can handle unknown values and parameters by taking every execution path, which is cool. But it's also possible to get stuck in infinite loops and debugging multiple execution paths is tricky.

