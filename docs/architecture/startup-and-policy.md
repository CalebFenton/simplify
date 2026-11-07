# Logging Ownership

## Packaged Logging Ownership

SmaliVM packages only the SLF4J API, with no production logging provider
or root `logback.xml`. Embedding applications own provider and configuration selection.

Simplify and SDBG each supply their own `logback.xml` with a WARN root level and a Logback provider.
The demo supplies SLF4J NOP instead. Application distribution classpaths therefore do not inherit
a second configuration from the library. SmaliVM's WARN configuration is test-only
(`smalivm/src/test/resources/logback-test.xml`); Simplify uses that shared test resource without
packaging it in production or duplicating it in its own test resources.

Simplify's quiet/verbosity controls and SDBG initialization intentionally use Logback-specific APIs.
That is an application choice, not a requirement imposed by the library. The retired engine's
logger-dependent consensus code, BeanUtils dependency, and associated Commons Logging collision
are no longer part of the production execution path.
