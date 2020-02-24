val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val kodeinDiVersion = "6.5.1"

plugins {
    application
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

group = "com.argus3000"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("org.kodein.di:kodein-di-generic-jvm:$kodeinDiVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinDiVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation("io.ktor:ktor-serialization:$ktor_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}