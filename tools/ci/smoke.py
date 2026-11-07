#!/usr/bin/env python3
"""Exercise built packages, never Gradle's test classpath (Python stdlib only)."""

import os
from pathlib import Path
import re
import shlex
import shutil
import subprocess
import sys
import tempfile
import zipfile


ROOT = Path(__file__).resolve().parents[2]
METHOD = "LPackagedSmoke;->multiply()J"
RESULT = "Known(raw=4294967294, type=J)"
ANSI = re.compile(r"\x1b\[[0-?]*[ -/]*[@-~]")
TIMEOUT = 180


class SmokeFailure(Exception):
    pass


def require(condition, message):
    if not condition:
        raise SmokeFailure(message)


def required_file(path):
    require(path.is_file(), f"Missing built artifact or fixture: {path}")
    return path


class Smoke:
    def __init__(self, scratch):
        self.scratch = scratch
        self.env = os.environ.copy()
        java_home = self.env.get("JAVA_HOME")
        self.java = str(required_file(Path(java_home) / "bin/java")) if java_home else shutil.which("java")
        require(self.java, "java is not on PATH and JAVA_HOME is unset")
        # Java does not use TMPDIR itself. Keep its temporary files and console
        # preferences inside the same automatically removed scratch directory.
        options = [f"-Djava.io.tmpdir={scratch}", f"-Duser.home={scratch}",
                   "-Dorg.jline.terminal.dumb=true", "-Dpicocli.ansi=false"]
        self.env["JAVA_TOOL_OPTIONS"] = " ".join(
            [self.env.get("JAVA_TOOL_OPTIONS", "")] + ['"' + option + '"' for option in options]
        )
        self.env["TMPDIR"] = str(scratch)
        self.env["TERM"] = "dumb"
        self.last_command = ""
        self.last_output = ""
        self.scenarios = 0

    def run(self, label, command, args=(), stdin=""):
        command = [str(part) for part in [*command, *args]]
        self.last_command = shlex.join(command)
        self.last_output = ""
        print(f"[smoke] {label}", flush=True)
        try:
            result = subprocess.run(command, input=stdin, text=True,
                                    stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                                    cwd=self.scratch, env=self.env, timeout=TIMEOUT)
        except subprocess.TimeoutExpired as error:
            output = error.stdout or b""
            self.last_output = output.decode(errors="replace") if isinstance(output, bytes) else output
            raise SmokeFailure(f"{label}: exceeded {TIMEOUT}s") from error
        self.last_output = ANSI.sub("", result.stdout).replace("\r", "")
        require(result.returncode == 0, f"{label}: exit status {result.returncode}")
        self.scenarios += 1
        return self.last_output

    def fat(self, app):
        return [self.java, "-jar", required_file(ROOT / app / "build/libs" / f"{app}.jar")]

    def distribution(self, app):
        archives = sorted((ROOT / app / "build/distributions").glob(f"{app}-*.zip"))
        require(len(archives) == 1,
                f"Expected one {app} ZIP distribution, found {len(archives)}; use clean build outputs")
        archive = archives[0]
        destination = self.scratch / app
        destination.mkdir()
        with zipfile.ZipFile(archive) as source:
            for entry in source.infolist():
                target = (destination / entry.filename).resolve()
                require(target.is_relative_to(destination), f"Unsafe ZIP path: {entry.filename}")
                source.extract(entry, destination)
                # zipfile does not restore Unix executable permission bits.
                mode = entry.external_attr >> 16
                if mode:
                    target.chmod(mode & 0o777)
        home = destination / archive.stem
        launcher = required_file(home / "bin" / app)
        require(os.access(launcher, os.X_OK), f"ZIP launcher is not executable: {launcher}")
        required_file(home / "lib" / f"{home.name}.jar")
        return home, [launcher]

    def optimized(self, app, home):
        version = home.name[len(app) + 1:]
        vm = required_file(ROOT / "smalivm/build/libs" / f"smalivm-{version}-proguard.jar")
        original = required_file(ROOT / "smalivm/build/libs" / f"smalivm-{version}-all.jar")
        with zipfile.ZipFile(original) as archive:
            bundled_classes = {name for name in archive.namelist()
                               if name.endswith(".class") and not name.endswith("module-info.class")}
        jars = sorted((home / "lib").glob("*.jar"))
        require(any(jar.name.startswith("smalivm") for jar in jars),
                f"Distribution lacks ordinary VM dependency: {home}")
        dependencies = []
        for jar in jars:
            if jar.name.startswith("smalivm"):
                continue
            with zipfile.ZipFile(jar) as archive:
                names = archive.namelist()
                require(not any(name.startswith("org/cf/smalivm/") for name in names),
                        f"Unoptimized VM classes contaminate isolated classpath: {jar}")
                classes = {name for name in names
                           if name.endswith(".class") and not name.endswith("module-info.class")}
            # The optimized VM must supply its own bundled libraries too.
            # Retain only application dependencies outside the original VM bundle.
            if not classes or not classes.issubset(bundled_classes):
                dependencies.append(jar)
        classpath = os.pathsep.join(str(jar) for jar in [vm, *dependencies])
        return [self.java, "--add-opens=java.base/java.lang=ALL-UNNAMED",
                "-cp", classpath, f"org.cf.{app}.Main"]

    def debugger(self, label, command, source, interactive=False):
        commands = "continue\n"
        if interactive:
            # Indexes count executable instructions, not DEX code units. Math
            # is authorized for host reflection, so stepping its invoke returns
            # to the caller before move-result-wide.
            commands = ("break 2\nbreak 4\ncontinue\nprint v0\nprint v1\n"
                        "step\nwhere\ncontinue\nprint v2\ncontinue\n")
        output = self.run(label, command, [source, METHOD], commands)
        require("execution finished: Complete" in output, f"{label}: incomplete execution")
        # DEX const-wide has no intrinsic Java type. Check the returned bits,
        # not the debugger's inferred register type after constant folding.
        require(re.search(r"Returned\(value=Known\(raw=4294967294, type=[^)]*\)\)", output),
                f"{label}: wrong wide return value")
        require(not re.search(r"(?:Threw|Aborted)\(", output), f"{label}: exceptional outcome")
        if interactive:
            for expected in [f"hit breakpoint {METHOD} @ 2", f"hit breakpoint {METHOD} @ 4",
                             "v0: Known(raw=2147483647, type=I)",
                             "v1: Known(raw=2, type=I)", f"v2: {RESULT}",
                             f"#3 in {METHOD}"]:
                require(expected in output, f"{label}: missing debugger observation: {expected}")

    def framework_step(self, label, command):
        source = ROOT / "sdbg/src/test/resources/debugger"
        output = self.run(label, command,
                          [source, "LDebugFixture;->frameworkCall()Ljava/lang/StringBuilder;"],
                          "break 3\ncontinue\nstep\nwhere\nprint p1\n")
        require("#0 in Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;" in output,
                f"{label}: did not enter the captured framework body")
        require(re.search(r"p1\(r\d+\): Known\(raw=2, type=[^)]*\)", output),
                f"{label}: framework frame lost the caller's wide argument")
        require(not re.search(r"(?:Threw|Aborted)\(", output), f"{label}: exceptional outcome")

    def demo(self, label, command):
        output = self.run(label, command, [ROOT / "demoapp/resources"])
        for expected in ["helloWorld(): hello,world!", "stringBuilder(): 123abc",
                         "printParameter(), param = 42",
                         "Guest main: Complete", "Known print parameter 42: Complete",
                         "Unknown parameter: Complete", "Known parameter 10: Complete",
                         "Unknown parameter: return consensus = Unknown(type=I)",
                         "Known parameter 10: return consensus = Known(raw=10, type=I)"]:
            require(expected in output, f"{label}: missing demo result: {expected}")
        require(not re.search(r"(?:Threw|Aborted)\(", output), f"{label}: exceptional demo outcome")

    def simplify(self, label, command, source, output):
        text = self.run(label, command, [source, "--output", output, "--output-api-level", "20"])
        for key, value in [("total methods", 1), ("optimized methods", 1),
                           ("failed methods", 0), ("pass-limit methods", 0)]:
            require(re.search(rf"(?m)^\s*{key} = {value}\s*$", text),
                    f"{label}: unexpected {key}")
        required_file(output)
        if output.suffix == ".apk":
            with zipfile.ZipFile(output) as archive:
                magic = archive.read("classes.dex")[:8]
        else:
            with output.open("rb") as stream:
                magic = stream.read(8)
        require(magic == b"dex\n035\0", f"{label}: output is not DEX035: {magic!r}")


def apk_marker(archive):
    entry = archive.getinfo("assets/marker.bin")
    return (archive.comment, archive.read(entry), entry.date_time, entry.compress_type,
            entry.comment, entry.extra, entry.CRC, entry.file_size)


def matrix(smoke):
    packages = {app: smoke.distribution(app) for app in ("simplify", "demoapp", "sdbg")}
    fixture = required_file(ROOT / "tools/ci/fixtures/PackagedSmoke.smali").parent
    smoke.debugger("sdbg fat JAR: original modern call, break/continue/step/registers",
                   smoke.fat("sdbg"), fixture, interactive=True)
    smoke.framework_step("sdbg fat JAR: enter captured framework body", smoke.fat("sdbg"))
    optimized_debugger = smoke.optimized("sdbg", packages["sdbg"][0])
    smoke.debugger("sdbg isolated ProGuard VM: original modern call",
                   optimized_debugger, fixture, interactive=True)
    smoke.framework_step("sdbg isolated ProGuard VM: enter captured framework body",
                         optimized_debugger)
    for form, command in [("fat JAR", smoke.fat("demoapp")),
                          ("ZIP launcher", packages["demoapp"][1]),
                          ("isolated ProGuard VM", smoke.optimized("demoapp", packages["demoapp"][0]))]:
        smoke.demo(f"demoapp {form}: normal embedding examples", command)

    first = smoke.scratch / "smali.dex"
    second = smoke.scratch / "dex.dex"
    optimized = smoke.scratch / "proguard.dex"
    apk = smoke.scratch / "input.apk"
    output_apk = smoke.scratch / "output.apk"
    smoke.simplify("Simplify fat JAR: Smali -> DEX035", smoke.fat("simplify"), fixture, first)
    smoke.simplify("Simplify ZIP launcher: DEX -> DEX035", packages["simplify"][1], first, second)
    with zipfile.ZipFile(apk, "w") as archive:
        archive.comment = b"packaged smoke archive metadata"
        archive.write(first, "classes.dex")
        marker = zipfile.ZipInfo("assets/marker.bin", date_time=(2020, 9, 13, 12, 26, 40))
        marker.compress_type = zipfile.ZIP_STORED
        marker.comment = b"preserved resource metadata"
        marker.extra = b"\xfe\xca\x04\x00test"
        archive.writestr(marker, b"\x00Simplify APK marker\xff\n")
    smoke.simplify("Simplify ZIP launcher: APK -> APK", packages["simplify"][1], apk, output_apk)
    with zipfile.ZipFile(apk) as before, zipfile.ZipFile(output_apk) as after:
        require(before.namelist() == after.namelist(), "APK entry names/order changed")
        require(apk_marker(before) == apk_marker(after), "APK marker bytes or ZIP metadata changed")
    smoke.simplify("Simplify isolated ProGuard VM: original Smali -> DEX035",
                   smoke.optimized("simplify", packages["simplify"][0]), fixture, optimized)
    for name, source in [("Smali output", first), ("DEX output", second),
                         ("APK output", output_apk), ("ProGuard output", optimized)]:
        smoke.debugger(f"sdbg ZIP launcher: execute {name}", packages["sdbg"][1], source)


def main():
    smoke = None
    try:
        with tempfile.TemporaryDirectory(prefix="simplify-smoke-") as directory:
            smoke = Smoke(Path(directory).resolve())
            matrix(smoke)
            print(f"[smoke] PASS: {smoke.scenarios} packaged executions; fat JARs, ZIP launchers, "
                  "isolated ProGuard VM, Smali/DEX035/APK, wide results and APK metadata.")
            print("[smoke] Uses existing build artifacts and repository demo fixtures; "
                  "ZIP launchers require a POSIX runner. TAR duplicates are not rerun.")
        return 0
    except (SmokeFailure, OSError, zipfile.BadZipFile, KeyError) as error:
        print(f"[smoke] FAIL: {error}", file=sys.stderr)
        if smoke and smoke.last_command:
            print(f"Command: {smoke.last_command}\n--- combined output ---\n{smoke.last_output}",
                  file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
