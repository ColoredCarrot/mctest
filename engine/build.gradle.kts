import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    implementation(project(":api"))
    implementation(project(":runtime-sdk"))

    implementation("org.junit.platform:junit-platform-engine:1.9.0")

    implementation("commons-codec:commons-codec:1.15")
    implementation("org.ow2.asm:asm:9.3") // we only need this for Type.getMethodDescriptor -> might get rid of this in the future

//    compileOnly(files("vendor/spigot-1.18.2-R0.1-SNAPSHOT.jar"))
//    implementation(fileTree("vendor") { include("*.jar") })
//    compileOnly(fileTree("vendor") { include("*.jar") })

//    implementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs = listOf("-Xcontext-receivers")
//    }
//}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
