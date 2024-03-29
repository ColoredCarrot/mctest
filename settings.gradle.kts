rootProject.name = "mctest"

include("api")
include("api-assertj")
include("mcproto:api")
include("mcproto:mc_1_18_2")
include("runtime-sdk")
include("plugin")
include("runtime")
include("engine")
include("example")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.opencollab.dev/maven-releases/")
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    }
}
