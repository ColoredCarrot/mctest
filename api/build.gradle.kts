plugins {
    id("java")
    kotlin("jvm") version "1.6.21"
}

group = "info.voidev.mctest"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
}

dependencies {
    api("org.junit.platform:junit-platform-commons:1.9.0")
    api("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")//TODO is there a more appropriate config for this?

    // In case we want to test the api module itself:
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
