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
val kotlinCouroutinesVersion by extra("1.3.5")
val kotlinSerializationVersion by extra("0.20.0")

val discord4jVersion by extra("3.0.13")
val kotlinLoggingVersion by extra("1.7.9")
val logbackVersion by extra("1.2.3")
val spekVersion by extra("2.0.10")

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCouroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCouroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$kotlinSerializationVersion")

    implementation("com.discord4j:discord4j-core:$discord4jVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test", version = kotlinVersion)
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}
