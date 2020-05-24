import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
}

group = "com.github.frozensync"
version = "1.0-SNAPSHOT"

val kotlinVersion by extra("1.3.70")
val kotlinCoroutinesVersion by extra("1.3.5")
val kotlinCollectionsImmutableVersion by extra("0.3.2")

val koinVersion by extra("2.1.5")
val discord4jVersion by extra("3.0.13")
val kMongoVersion by extra("4.0.1")

val kotlinLoggingVersion by extra("1.7.9")
val logbackVersion by extra("1.2.3")
val spekVersion by extra("2.0.10")

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:$kotlinCollectionsImmutableVersion")

    implementation("org.koin:koin-core:$koinVersion")
    implementation("com.discord4j:discord4j-core:$discord4jVersion")
    implementation("org.litote.kmongo:kmongo-coroutine-native:${kMongoVersion}")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test", version = kotlinVersion)
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xinline-classes"
    )
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}
