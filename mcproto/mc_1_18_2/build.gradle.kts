plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "info.voidev.mcproto"
version = "0.1.0"

dependencies {
    compileOnly(project(":mcproto:api"))
    implementation("com.github.steveice10:mcprotocollib:1.18.2-1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    dependencies {
        include(dependency("com.github.steveice10:mcprotocollib"))
    }
    relocate("com.github.steveice10.mc.protocol", "info.voidev.mcproto.mc_1_18_2.steveice10")
}
