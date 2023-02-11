plugins {
    id("com.github.johnrengelman.shadow")
    java
    kotlin("jvm")
    `maven-publish`
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
    // TODO: Extract this dependency block into an "all" or "impl" subproject in :mcproto
    runtimeOnly(project(":mcproto:mc_1_18_2", configuration = "shadow"))
    // TODO: Can we programmatically collect the first-level transitive dependencies of mcprotcollib?
    //  Although, thinking about it, it would probably be cleaner to just shadow all those dependencies into the version-specific proto JARs as well
    runtimeOnly("com.github.GeyserMC:mcauthlib:6f3d6aada5")
    runtimeOnly("com.github.GeyserMC:opennbt:1.4")
    runtimeOnly("com.github.GeyserMC:packetlib:2.1")
    runtimeOnly("net.kyori:adventure-text-serializer-gson-legacy-impl:4.9.3")

    implementation(project(":runtime-sdk"))
    implementation("org.ow2.asm:asm:9.4")
    implementation("org.ow2.asm:asm-util:9.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    // fastutil is provided by the server JAR, so we needn't include it ourselves.
    // Though it might still become a good idea to do so anyway.
    compileOnly("it.unimi.dsi:fastutil:8.5.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Premain-Class" to "info.voidev.mctest.runtime.agent.MctestRuntimeAgent",
            "Main-Class" to "info.voidev.mctest.runtime.MctestRuntimeMainKt",
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("runtime") {
            project.shadow.component(this)
        }
    }
}
