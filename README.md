# MCTest

A testing framework for Spigot plugins, implemented as a Jupiter (JUnit 5) test engine.

**Highlights:**

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

## Implementation

### Terminology

- **Engine:** The Jupiter test engine responsible for finding test methods and supervising the runtime.
- **Runtime:** The runtime installed on the Minecraft server's JVM.

### Communication between Engine and Runtime

We use Java's Runtime Method Invocation (RMI) mechanism.

1. The engine (E) sets up a registry on some free port (say 1099)
2. E starts up the runtime (R), passing the registry's port
3. R gets a reference to the registry and registers its service
4. R passes a signal via STDOUT to E
5. E looks up the runtime service (knowing it's available because of the signal)

Bidirectional communication is now established.

## Ideas for the future

1. A server pool; a JVM process pool to parallelize test execution
2. In the same vein, a server daemon running continuously across test runs to keep the server alive
3. Integration with Testcontainers for testing plugins with DB connections
