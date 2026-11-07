# SmaliVM Instructions
## Verification Order
1. Run focused tests: `./gradlew :smalivm:test --tests '<test-pattern>' --rerun-tasks --no-build-cache`.
2. Assemble the local device fixture: `tools/art-runner/assemble.sh --force`.
3. Run the full gate: `./gradlew --stop && ./gradlew :smalivm:clean && ./gradlew :smalivm:test --rerun-tasks --no-build-cache`.
4. Confirm `:smalivm:test` executed; `UP-TO-DATE` or `FROM-CACHE` does not verify a change.
5. Read `smalivm/build/test-results/test/TEST-*.xml` for measured results; do not use a remembered count.
## Semantic Reasoning Workflow
1. Construct the smallest concrete Java or Smali witness that exposes the disputed behavior.
2. State the current outcome and correct outcome before selecting a code change.
3. Ground the correct outcome in runtime measurement, specification, or an independently derived invariant.
4. Evaluate every feasible path, constant, alias, type, exception, and effect for the witness.
5. Compare the original engine behavior with the target architecture; agreement can preserve a shared defect.
6. Evaluate CPU cost, memory retention, and expansion of paths or `Unknown` values.
7. Use ART or emulator measurement when Java, Dalvik, or ART behavior remains unclear.
8. Add behavioral evidence and name a mutation that makes the new test fail.
## Fix Selection
- Prefer the platonic correct fix over the smallest patch or a temporary compatibility path.
- Add complexity only when it is load-bearing for correctness, precision, fewer `Unknown` values, CPU, or memory.
- Keep new state and abstraction proportional to the invariant they enforce.
- Adjudicate oracle behavior independently; an oracle defect is not a compatibility requirement.
## Reviewer Findings
- Give a concrete witness and its effect on deobfuscation or path correctness.
- State the original behavior, target invariant, required complexity, and test that detects the defect.
- Separate semantic regressions from architecture preferences and implementation-only differences.
## Boundaries
**Always**: Require independent behavioral and mutation evidence for aliases, exceptions, effects, and path outcomes.
**Ask first**: Stop before nontrivial reviewer-proposed architecture that the semantic workflow cannot ground.
**Never**: Add arbitrary path-width limits; model actual uncertainty and resource limits explicitly.
**Never**: Add eager whole-state copies; preserve unchanged state through safe sharing.
**Never**: Derive expected test results from current implementation output.
**Never**: Preserve an oracle defect without explicit semantic adjudication.
