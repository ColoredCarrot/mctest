# MCTest

[![Build](https://github.com/ColoredCarrot/mctest/actions/workflows/build.yml/badge.svg)](https://github.com/ColoredCarrot/mctest/actions/workflows/build.yml)

A testing framework for Spigot plugins, implemented as a Jupiter (JUnit 5) test engine.

**Highlights:**

- Full IDE integration: Since MCTest acts as a Jupiter test engine,
  IDEs like IntelliJ automatically integrate with MCTest just like any other test engine (like JUnit).
- Physical test players: Declaring a `TestPlayer` parameter
  will join a physical Minecraft client to the server
  and give you access to the Bukkit `Player` as well as a fully capable client.
- Tick yielding: Declaring a test method as `suspend`
  allows you to skip a tick without breaking up the code flow.
  Using this method, complex test scenarios are easily modeled.

## Usage

```kotlin
class ForwardCommandTest {
    @MCTest
    suspend fun TestScope.`player teleports themself forward`(
        player: TestPlayer,
    ) {
        val oldLocation = player.location

        player.client.say("/forward")

        assertThat(oldLocation.distance(player.location))
            .isCloseTo(1.0, withinEpsilon)
    }
}
```

## Installation

Using `build.gradle.kts`:

```kotlin
repositories {
    /* ... */
    maven("https://maven.pkg.github.com/ColoredCarrot/mctest")
}

dependencies {
    /* ... */
    testImplementation("info.voidev.mctest:api:${mctestVersion}")
    testImplementation("info.voidev.mctest:api-assertj:${mctestVersion}") // Optional
    testRuntimeOnly("info.voidev.mctest:engine:${mctestVersion}")
}
```

## Implementation

### Terminology

- **Engine:** The Jupiter test engine responsible for finding test methods and supervising the runtime.
- **Runtime:** The runtime installed on the Minecraft server's JVM.

### Communication between Engine and Runtime

We use Java's Runtime Method Invocation (RMI) mechanism.

1. The engine (E) sets up a registry on some free port (say 1099)
2. E starts up the runtime (R), passing the registry's port
3. R gets a reference to the registry and registers its service
4. R sends a signal via RMI to E
5. E looks up the runtime service (knowing it's available because of the signal)

Bidirectional communication is now established.

## Ideas for the future

1. A server pool; a JVM process pool to parallelize test execution
2. In the same vein, a server daemon running continuously across test runs to keep the server alive
3. Integration with Testcontainers for testing plugins with DB connections
4. Dynamically discover the required Spigot version from the testee plugin.yml
