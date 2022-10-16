plugins {
    id("java")
    kotlin("jvm") version "1.6.21"
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
    api(project(":api"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
