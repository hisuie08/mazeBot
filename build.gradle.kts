plugins {
    java
    kotlin("jvm") version "1.5.20"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.nashiroaoi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("net.dv8tion:JDA:4.3.0_277")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("stage"){
    dependsOn("clean","shadowJar")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>{
    archiveFileName.set("bot.jar")
}

application{
    mainClass.set("${group}.${rootProject.name}.MainKt")
}