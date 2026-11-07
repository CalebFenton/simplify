#!/usr/bin/env python3
"""Summarize Gradle's recorded test cases without counting suite containers."""

import argparse
from dataclasses import dataclass
import html
import os
from pathlib import Path
import sys
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[2]
MODULES = ("smalivm", "simplify", "sdbg")


@dataclass
class Counts:
    passed: int = 0
    failed: int = 0
    errors: int = 0
    skipped: int = 0

    @property
    def total(self):
        return self.passed + self.failed + self.errors + self.skipped

    def cells(self):
        return [self.passed, self.failed, self.errors, self.skipped, self.total]

    def add(self, other):
        self.passed += other.passed
        self.failed += other.failed
        self.errors += other.errors
        self.skipped += other.skipped


def tag_name(element):
    return element.tag.rsplit("}", 1)[-1]


def read_counts(path):
    root = ET.parse(path).getroot()
    if tag_name(root) not in ("testsuite", "testsuites"):
        raise ValueError(f"unexpected XML root: {root.tag}")

    counts = Counts()
    # Gradle records dynamic/parameterized tests as testcase elements, and
    # nested classes can have separate TEST-*.xml files. Suite attributes may
    # include container totals: never add them to the actual testcase count.
    for case in root.iter():
        if tag_name(case) != "testcase":
            continue
        outcomes = {tag_name(child) for child in case}
        if "error" in outcomes:
            counts.errors += 1
        elif "failure" in outcomes:
            counts.failed += 1
        elif "skipped" in outcomes:
            counts.skipped += 1
        else:
            counts.passed += 1
    return counts


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--require-results",
        action="store_true",
        help="fail if any expected module has no recorded test cases",
    )
    args = parser.parse_args()

    lines = [
        "## Gradle test results",
        "",
        "| Module | Passed | Failed | Errors | Skipped | Total | Status |",
        "| --- | ---: | ---: | ---: | ---: | ---: | --- |",
    ]
    diagnostics = []
    total = Counts()
    failed = False
    incomplete = False

    for module in MODULES:
        reports = sorted((ROOT / module / "build/test-results").rglob("TEST-*.xml"))
        counts = Counts()
        invalid = 0
        for report in reports:
            try:
                counts.add(read_counts(report))
            except (ET.ParseError, OSError, ValueError) as error:
                invalid += 1
                diagnostics.append(f"{report.relative_to(ROOT)}: {error}")

        if invalid:
            status = f"Incomplete: {invalid} unreadable/invalid XML report(s)"
            incomplete = failed = True
        elif not reports:
            status = "Not available / not run: no XML reports"
        elif not counts.total:
            status = "Not available / not run: XML contains no test cases"
        else:
            status = f"{len(reports)} XML report(s)"

        if not counts.total:
            incomplete = True
            if args.require_results:
                failed = True
                diagnostics.append(f"{module}: expected test results are missing or empty")
        if counts.failed or counts.errors:
            failed = True
        total.add(counts)
        cells = counts.cells() if counts.total else ["—"] * 5
        lines.append("| " + " | ".join(map(str, [module, *cells, status])) + " |")

    lines.append("| demoapp | — | — | — | — | — | No tests defined (not a passing suite) |")
    total_cells = total.cells() if total.total else ["—"] * 5
    total_status = "Incomplete; available results only" if incomplete else "Recorded test cases"
    lines.append("| " + " | ".join(map(str, ["Total available", *total_cells, total_status])) + " |")
    lines.extend([
        "",
        "Counts come from recorded testcase elements, not suite/container totals. "
        "Skipped cases did not run. Missing results are not passing tests. "
        "Gradle may restore reports from its build cache; task logs distinguish "
        "executed tests from FROM-CACHE results.",
    ])
    if diagnostics:
        lines.extend(["", "### Report problems", "", "<pre>"])
        lines.extend(html.escape(message) for message in diagnostics)
        lines.append("</pre>")

    summary = "\n".join(lines) + "\n"
    print(summary, end="")
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as output:
            output.write(summary)
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
