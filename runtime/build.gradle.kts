plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    kotlin("jvm")
    `maven-publish`
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
    implementation(project(":runtime-sdk"))
    implementation("org.ow2.asm:asm:9.4")
    implementation("org.ow2.asm:asm-util:9.4")
    implementation("com.github.steveice10:mcprotocollib:1.18.2-1")
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
