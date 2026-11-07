# The ART runner's batch dispatcher.
#
# WHY THIS EXISTS: one `dalvikvm` process costs ~209ms, but 1000 fixtures inside one
# process cost 0.73s total (~0.7ms each). So the unit of work is a BATCH: the runner
# passes N fixture class names as argv and this class reflects into each one.
#
# WHY IT IS SMALI AND NOT JAVA: smali is the only assembler this repo already depends
# on (`com.android.tools.smali:smali:3.0.10` is resolved by `:smalivm`). A Java
# dispatcher would need `d8` -- a 19MB download and a second toolchain -- to buy
# nothing, because all the interesting logic belongs in the FIXTURES, which must be
# smali regardless. It is also assembled at the same API level as the fixtures, which
# is load-bearing (see assemble.sh).
#
# THE FIXTURE CONTRACT: `public static Object run()`. Boxing the result is the
# fixture's job, because that keeps ONE output grammar for value observations and
# throw observations alike, and because the interesting primitives (an `int` that is
# really a masked shift result, a `long` that overflowed) survive boxing unchanged.
#
# THE OUTPUT GRAMMAR, one line per record, tab-separated:
#     RUNTIME   <java.vm.name>            <java.vm.version>
#     FIXTURE   <class name>              OK       <describe(result)>
#     FIXTURE   <class name>              THREW    <throwable class>   <message>
#     DISPATCH_END
#
# `DISPATCH_END` IS THE SENTINEL AND IT IS NOT DECORATION. ART's shutdown thread
# reliably crashes AFTER main returns, so the process exit status is ALWAYS nonzero
# (134 or 139) even on a completely successful run. stdout is unaffected -- verified
# 20/20 trials, and a 2001-line output arrived complete through a pipe -- so the
# runner gates on this line and never on `$?`. Do NOT "fix" the exit status with
# `System.exit()`: measured, that trades the harmless post-main crash for an EARLIER
# crash that loses output.
#
# Every emitted field is passed through `oneLine`, so a throwable message containing
# a newline or a tab cannot forge a record boundary. ART's verifier diagnostics are
# the realistic source of such a message and they are the single highest-value thing
# this oracle produces, so they must not be able to corrupt the protocol.

.class public LRunFixtures;
.super Ljava/lang/Object;


.method public static main([Ljava/lang/String;)V
    .locals 5

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;

    # Provenance first, so a captured batch is self-describing without the runner's help.
    const-string v1, "RUNTIME\t"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    const-string v1, "java.vm.name"
    invoke-static {v1}, Ljava/lang/System;->getProperty(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v1
    invoke-static {v1}, LRunFixtures;->oneLine(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v1
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    const-string v1, "\t"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    const-string v1, "java.vm.version"
    invoke-static {v1}, Ljava/lang/System;->getProperty(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v1
    invoke-static {v1}, LRunFixtures;->oneLine(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v1
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    const/4 v1, 0x0
    array-length v2, p0

    :loop
    if-ge v1, v2, :loop_end

    aget-object v3, p0, v1

    const-string v4, "FIXTURE\t"
    invoke-virtual {v0, v4}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    invoke-virtual {v0, v3}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    const-string v4, "\t"
    invoke-virtual {v0, v4}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V

    invoke-static {v3}, LRunFixtures;->observe(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v4
    invoke-virtual {v0, v4}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    add-int/lit8 v1, v1, 0x1
    goto :loop

    :loop_end
    const-string v4, "DISPATCH_END"
    invoke-virtual {v0, v4}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    return-void
.end method


# Loads the class, calls its `run()`, and renders EITHER outcome as one field group.
#
# The try range deliberately covers `describe` as well as the reflective call, so a
# fixture whose returned object has a throwing `toString()` is reported as THREW
# instead of killing the whole batch. That is a slight misattribution (the fixture's
# `run` did succeed) traded for never losing the other N-1 records; no fixture in the
# corpus returns such an object, since they all return boxed primitives or strings.
#
# `Class.forName` is where a VERIFIER-INVALID fixture reports in: ART sets the whole
# class to kErrorResolved and the load throws a CATCHABLE `java.lang.VerifyError`
# whose message is ART's exact verifier diagnostic. That is why the catch is
# `Throwable` and not `Exception`.
.method private static observe(Ljava/lang/String;)Ljava/lang/String;
    .locals 8

    :try_start
    invoke-static {p0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0

    const-string v1, "run"
    const/4 v2, 0x0
    new-array v3, v2, [Ljava/lang/Class;
    invoke-virtual {v0, v1, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v4

    const/4 v2, 0x0
    new-array v5, v2, [Ljava/lang/Object;
    const/4 v6, 0x0
    invoke-virtual {v4, v6, v5}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v7

    const-string v0, "OK\t"
    invoke-static {v7}, LRunFixtures;->describe(Ljava/lang/Object;)Ljava/lang/String;
    move-result-object v1
    invoke-virtual {v0, v1}, Ljava/lang/String;->concat(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v0
    :try_end

    return-object v0

    :catch_all
    move-exception v0
    invoke-static {v0}, LRunFixtures;->describeThrow(Ljava/lang/Throwable;)Ljava/lang/String;
    move-result-object v1
    return-object v1

    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :catch_all
.end method


# Renders a returned value as `<class name>(<toString>)`, or the literal `null`.
#
# The class name is reported SEPARATELY from the value because half the questions this
# oracle is built to answer are about the TYPE that came back, not the bits: reading
# back an element that was stored into a `String[]` and finding `java.lang.Object` is
# exactly how the AOT unsoundness in README.md was caught.
.method private static describe(Ljava/lang/Object;)Ljava/lang/String;
    .locals 3

    if-nez p0, :not_null
    const-string v0, "null"
    return-object v0

    :not_null
    new-instance v0, Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v1
    invoke-virtual {v1}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v1
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "("
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {p0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
    move-result-object v1
    invoke-static {v1}, LRunFixtures;->oneLine(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v1
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, ")"
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v2
    return-object v2
.end method


# Renders a throw as `THREW<tab><class><tab><message>`.
#
# Reflection wraps whatever the fixture threw in an `InvocationTargetException`, so
# that one wrapper is unwrapped -- otherwise every single throw observation would read
# `java.lang.reflect.InvocationTargetException` and the oracle would answer nothing.
# Nothing else is unwrapped: a `VerifyError` from `Class.forName` and an
# `ExceptionInInitializerError` from a `<clinit>` arrive unwrapped and must stay that
# way, because WHICH of those two arrives is itself an observation.
.method private static describeThrow(Ljava/lang/Throwable;)Ljava/lang/String;
    .locals 5

    move-object v0, p0

    instance-of v1, v0, Ljava/lang/reflect/InvocationTargetException;
    if-eqz v1, :unwrapped
    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v1
    if-eqz v1, :unwrapped
    move-object v0, v1

    :unwrapped
    new-instance v2, Ljava/lang/StringBuilder;
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "THREW\t"
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v3
    invoke-virtual {v3}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v3
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v3, "\t"
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/Throwable;->getMessage()Ljava/lang/String;
    move-result-object v3
    invoke-static {v3}, LRunFixtures;->oneLine(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v3
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v4
    return-object v4
.end method


# Escapes the three characters that could forge a record boundary in the line-oriented,
# tab-separated protocol, and maps a null message to the literal text `null` (rather
# than letting `String.concat` throw an NPE on it).
.method private static oneLine(Ljava/lang/String;)Ljava/lang/String;
    .locals 3

    if-nez p0, :not_null
    const-string v0, "null"
    return-object v0

    :not_null
    const-string v0, "\n"
    const-string v1, "\\n"
    invoke-virtual {p0, v0, v1}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    move-result-object v2

    const-string v0, "\r"
    const-string v1, "\\r"
    invoke-virtual {v2, v0, v1}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    move-result-object v2

    const-string v0, "\t"
    const-string v1, "\\t"
    invoke-virtual {v2, v0, v1}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    move-result-object v2

    return-object v2
.end method
