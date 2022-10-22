plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
    kotlin("jvm") version "1.6.21"
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
    implementation(project(":runtime-sdk"))
    implementation("org.ow2.asm:asm:9.3")
    implementation("org.ow2.asm:asm-util:9.3")
    implementation("com.github.steveice10:mcprotocollib:1.18.2-1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    // fastutil is provided by the server JAR, so we needn't include it ourselves.
    // Though it might still become a good idea to do so anyway.
    compileOnly("it.unimi.dsi:fastutil:8.5.8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    manifest {
        attributes(
            "Premain-Class" to "info.voidev.mctest.runtime.agent.MctestRuntimeAgent",
            "Main-Class" to "info.voidev.mctest.runtime.MctestRuntimeMainKt",
        )
    }
}
