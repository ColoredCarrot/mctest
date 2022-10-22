plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = "info.voidev.mctest"
version = "0.1.0"

dependencies {
    api(project(":api"))
	api("org.assertj:assertj-core:3.23.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("api-assertj") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/ColoredCarrot/mctest")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
