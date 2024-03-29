import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("com.github.johnrengelman.shadow")
    java
    kotlin("jvm")
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
//    compileOnly(project(":runtime-sdk"))
//    compileOnly(project(":runtime"))
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<ProcessResources>("processResources") {
    filter<ReplaceTokens>("tokens" to mapOf(
        "mctest.version" to version.toString(),
    ))
}

tasks.shadowJar {
//    exclude(":engine")
}
