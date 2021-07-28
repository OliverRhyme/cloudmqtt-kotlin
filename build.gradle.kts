import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
}

group = "com.coldfire"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client:1.6.1")
    implementation("io.ktor:ktor-client-cio:1.6.1")
    implementation("io.ktor:ktor-client-auth:1.6.1")
    implementation("io.ktor:ktor-client-gson:1.6.1")
    implementation("io.ktor:ktor-client-jackson:1.6.1")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:1.6.1")
    //For runBlockingTest, CoroutineDispatcher etc.
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}