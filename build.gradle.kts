plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "de.thws.fiw"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
}

application {
    mainClass.set("de.thws.fiw.kotlin.mygrep.MygrepKt")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("mygrep")
    archiveVersion.set("")
}