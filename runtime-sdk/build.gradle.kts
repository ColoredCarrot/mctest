plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
    api(project(":api"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("runtime-sdk") {
            from(components["java"])
        }
    }
}
