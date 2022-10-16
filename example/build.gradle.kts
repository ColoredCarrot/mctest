import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
}

group = "info.voidev.mctest"
version = "0.1.0"

val mctestJava: String? by project

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    testCompileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    //TODO Apparently compileOnly doesn't automatically put it on testCompileOnly ??  Figure this out better

    testImplementation(project(":api"))
    testImplementation(project(":api-assertj"))
    testRuntimeOnly(project(":engine"))
}

tasks.getByName<KotlinCompile>("compileTestKotlin") {
    // Compile with parameter names for tests for a better experience
    kotlinOptions {
        javaParameters = true
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()

    // Configuration parameters for MCTest
    // We want to explicitly (re-)use a fixed directory as the test server directory.
    // By default, a temporary directory is created in the OS temp folder.
    systemProperties(
        "mctest.java" to mctestJava.orEmpty(),
        "mctest.server.dir" to project.buildDir.resolve("server-dir").absolutePath,
    )
}

tasks.getByName<ProcessResources>("processResources") {
    filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf(
        "version" to version.toString(),
    ))
}
