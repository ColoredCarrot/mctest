import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    kotlin("jvm")
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
//    compileOnly(project(":runtime-sdk"))
//    compileOnly(project(":runtime"))
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<ProcessResources>("processResources") {
    filter<ReplaceTokens>("tokens" to mapOf(
        "mctest.version" to version.toString(),
    ))
}

tasks.getByName<ShadowJar>("shadowJar") {
//    exclude(":engine")
}
