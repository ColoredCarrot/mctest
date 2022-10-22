plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = "info.voidev.mctest"
version = "0.1.0"

val theRuntime = configurations.create("theRuntime")

dependencies {
    implementation(project(":api"))
    implementation(project(":runtime-sdk"))

    implementation("org.junit.platform:junit-platform-engine:1.9.0")

    implementation("commons-codec:commons-codec:1.15")
    implementation("org.ow2.asm:asm:9.4") // we only need this for Type.getMethodDescriptor -> might get rid of this in the future

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    theRuntime(project(":runtime", "shadow"))
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    dependsOn(":runtime:shadowJar")

    val runtimeJarPath = theRuntime.asPath.split(File.pathSeparator, limit = 2).first()
    from(runtimeJarPath) {
        rename(Regex.escape(runtimeJarPath.substringAfterLast(File.separator)), "runtime.jar")
    }
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs = listOf("-Xcontext-receivers")
//    }
//}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("engine") {
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
