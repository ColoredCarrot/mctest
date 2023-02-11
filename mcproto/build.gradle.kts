plugins {
    kotlin("jvm")
}

group = "info.voidev.mcproto"
version = "0.1.0"

dependencies {
//    implementation(project(":mcproto:mc_1_18_2", configuration = "shadow"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}
