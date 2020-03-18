import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
}

group = "com.github.frozensync"
version = "1.0-SNAPSHOT"

val kotlinVersion by extra("1.3.70")

val discord4j by extra("3.0.6")
val kotlinLoggingVersion by extra("1.6.26")
val logbackVersion by extra("1.2.3")
val spekVersion by extra("2.0.2")

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation(group = "com.discord4j", name = "discord4j-core", version = discord4j)
//    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.0.2.RELEASE")

    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)

    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test", version = kotlinVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}