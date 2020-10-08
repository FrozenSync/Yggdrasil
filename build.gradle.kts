import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("jvm") version "1.4.0"
    kotlin("kapt") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.0"
}

group = "com.github.frozensync"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.github.frozensync.MainKt"
}

val kotlinVersion by extra("1.4.0")
val kotlinCoroutinesVersion by extra("1.3.9")
val kotlinCollectionsImmutableVersion by extra("0.3.2")

val koinVersion by extra("2.1.6")
val discord4jVersion by extra("3.1.1")
val kMongoVersion by extra("4.1.1")
val lavaPlayerVersion by extra("1.3.50")
val cliktVersion by extra("3.0.1")

val arrowVersion by extra("0.11.0")

val kotlinLoggingVersion by extra("1.8.3")
val logbackVersion by extra("1.2.3")
val spekVersion by extra("2.0.12")

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:$kotlinCollectionsImmutableVersion")

    implementation("org.koin:koin-core:$koinVersion")
    implementation("com.discord4j:discord4j-core:$discord4jVersion")
    implementation("org.litote.kmongo:kmongo-coroutine-native:${kMongoVersion}")
    implementation("com.sedmelluq:lavaplayer:${lavaPlayerVersion}")
    implementation("com.github.ajalt.clikt:clikt:${cliktVersion}")

    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    kapt("io.arrow-kt:arrow-meta:$arrowVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test", version = kotlinVersion)
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += listOf("-Xinline-classes", "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.6"
}
