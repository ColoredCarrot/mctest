## Test Discovery

Each test method is mapped to a `MethodTestDescriptor`,
which contains a set of `LeafTestDescriptor`.

For each test method, an allowable version range is calculated from
- its `@MCVersion` annotation or, if absent,
- globally configured version constraints.

Normally, tests are run against the latest compatible Minecraft version.

For example, consider the following test methods:

```kotlin
@MCTest
@MCVersion(min = "1.17", max = "1.19")
fun a()

@MCTest
@MCVersion(max = "1.18")
fun b()

@MCTest
@MCVersion(max = "1.13")
fun c()
```

In order to run all these tests, it is necessary to spin up at least 3 test servers.

### Matrix tests

It is possible to request MCTest to run all tests on all applicable Minecraft versions.
