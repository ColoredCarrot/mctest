# MCTest

[![Build](https://github.com/ColoredCarrot/mctest/actions/workflows/build.yml/badge.svg)](https://github.com/ColoredCarrot/mctest/actions/workflows/build.yml)

A testing framework for Spigot plugins, implemented as a Jupiter (JUnit 5) test engine.

#### Highlights:

- **Physical test server:** Spigot is started in a separate JVM.
  The plugin under test is loaded through Bukkit's normal plugin loading mechanism,
  from a JAR file&mdash;as such, all the features you're used to using work out-of-the-box.
- **Full IDE integration:** Since MCTest acts as a Jupiter test engine,
  IDEs like IntelliJ automatically integrate with MCTest just like any other test engine (like JUnit).
- **Physical test players:** Declaring a `TestPlayer` parameter
  will join a physical Minecraft client to the server
  and give you access to the Bukkit `Player` as well as a fully capable client.
- **Tick yielding (Kotlin only):** Declaring a test method as `suspend`
  allows you to skip a tick without breaking up the code flow.
  Using this method, complex test scenarios spanning many server ticks are easily modeled.

## Usage

From **Kotlin**, using a physical test player:

```kotlin
class TpCommandTest {
    @MCTest
    suspend fun `teleport one block forward`(player: TestPlayer) {
        // Given:
        val oldLocation = player.location

        // When:
        player.client.say("/tp ~1 ~ ~")

        // Then:
        val expectedLocation = oldLocation.clone().add(1.0, 0.0, 0.0)
        assertThat(player.location).isCloseTo(expectedLocation, within(1e-7))
    }
}
```

From **Java**, server-side only testing:

```java
public class SetBlockTest {
    @MCTest
    public void change_block_material() {
        // Given:
        var block = Bukkit.getWorld("world").getBlockAt(0, 0, 0);

        // When:
        block.setType(Material.GREEN_WOOL);

        // Then:
        assertThat(block.getType()).isEqualTo(Material.GREEN_WOOL);
    }
}
```

## Installation

Using `build.gradle.kts`:

```kotlin
repositories {
    /* ... */
    maven("https://jitpack.io")
}

dependencies {
    /* ... */
    testImplementation("com.github.ColoredCarrot.mctest:api:0.1.0")
    testImplementation("com.github.ColoredCarrot.mctest:api-assertj:0.1.0") // Optional
    testRuntimeOnly("com.github.ColoredCarrot.mctest:engine:0.1.0")
}
```

## Contributing

If you spot a bug, have an idea for a new feature or just find something that could use a little polish:
Please don't hesitate to [open an issue](https://github.com/ColoredCarrot/mctest/issues/new)
or get in touch privately.

If you want to contribute some code: Great!
I don't have a specific contribution process set-up just yet,
but feel free to open a pull request.

## How it Works

***Note:**
This section is a work-in-progress; details may be missing or outdated.
If anything catches your eye, please [open an issue](https://github.com/ColoredCarrot/mctest/issues/new)
(see "Contributing").*

### Terminology

- **Engine:** The Jupiter test engine responsible for finding test methods and supervising the runtime.
- **Runtime:** The runtime installed on the Minecraft server's JVM.

### Communication between Engine and Runtime

We use Java's Remote Method Invocation (RMI) mechanism.

1. The Engine (E) sets up an RMI registry on some free port (say 1099).
2. E starts up the Runtime (R), passing the registry's port.
3. R gets a reference to the registry and registers its `RuntimeService`.
4. R sends a signal via RMI to E.
5. E looks up the runtime service (knowing it's available because of the signal).

Bidirectional communication is now established.

### Classloading in the Runtime

The **MCTest bootstrap class loader** is a `URLClassLoader` configured with the Minecraft server JAR.
It is used to load `org.bukkit.craftbukkit.bootstrap.Main`,
on which the Runtime invokes Bukkit's `main()`.
The class loader instruments that `Main` class to replace Bukkit's class loader,
which is a `URLClassLoader` configured with the unbundled library JARs
(those in the server's `bundler` directory),
with the MCTest runtime class loader.

The **MCTest runtime class loader** is more complex.
It, too, is a `URLClassLoader` configured with the server library JARs,
but it also configures the entire application classpath,
which includes the Kotlin standard library and any other (transitive) dependencies of the Runtime.
Thereby, the *application class loader* is effectively replaced
and will, in fact, no longer be called.

The runtime class loader customizes the class loading process thusly:

1. Classes already loaded by this class loader, *not* by a different class loader, are re-used.
2. If no cached version is available, the server libraries as well as the Runtime's classpath are searched.
    1. Some classes are instrumented as described below.
3. If the class has not been found, a request is made to the Engine to fetch the class' *class file* as a byte array.
    1. The Engine looks up the class in its own class path, i.e. the *plugin-under-test*'s class path.
4. If the class has still not been found, a `ClassNotFoundException` is thrown.

#### Instrumented Classes

In addition to `org.bukkit.craftbukkit.bootstrap.Main`,
the following classes are instrumented by the runtime class loader:

- `org.bukkit.plugin.java.JavaPlugin`: Required to prevent conflicts with Bukkit's plugin class loader
  (*our* runtime class loader needs to be the one to load the plugin's classes).
- `org.bukkit.plugin.java.JavaPluginLoader`: See above.
- `org.bukkit.craftbukkit.{version}.CraftServer`: Install a "server started" callback.
- `net.minecraft.network.NetworkManager`: Snoop packets sent and received by the server.
  Required for client-server packet synchronization.

#### The Hard Barrier

There is a *hard barrier* between classes loaded by the MCTest bootstrap class loader (hereinafter *B-classes*)
and those loaded by the runtime class loader (hereinafter *R-classes*).

If an *R-class* wish to use a *B-class* `C`,
`C` will be loaded anew by the runtime class loader,
effectively creating a new, distinct *R-class* `C'`.
This is because, to the JVM, two classes are equal if and only if
their names *and their class loaders* are equal.

The following consequences are notable:

- Casting an object of type `C` to `C'` will throw a `ClassCastException`.
- Static fields initialized on `C` will not have been initialized on `C'`.

Therefore, it is surprisingly difficult to share data among *B-* and *R-classes*.
Some possibilities are system properties for simple, small data and
some form of IPC for all other cases.

*Note that all of this complexity is entirely hidden from MCTest users.*


### Ideas for the Future

1. Support plugin dependencies declared in `plugin.yml`
2. A server pool; a JVM process pool to parallelize test execution
3. In the same vein, a server daemon running continuously across test runs to keep the server alive
4. Integration with Testcontainers for testing plugins with DB connections
5. Dynamically discover the required Spigot version from the testee plugin.yml
