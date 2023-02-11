plugins {
    kotlin("jvm")
}

group = "info.voidev.mcproto"
version = "0.1.0"

dependencies {
    api("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT") // TODO should we get rid of this dependency here?

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}
