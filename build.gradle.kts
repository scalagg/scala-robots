import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.0.0"
    kotlin("kapt") version "2.0.0"
    kotlin("plugin.lombok") version "2.0.0"
    id("io.freefair.lombok") version "5.3.0"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

var currentBranch: String = grgit.branch.current().name
if (currentBranch != "master") {
    println("Starting in development mode")
}

allprojects {
    group = "gg.scala.robots"
    version = "1.4.8${
        if (currentBranch != "master") "-dev" else ""
    }"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.dmulloy2.net/repository/public/")

        configureScalaRepository()
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    kapt("gg.scala.commons:bukkit:4.0.2")
    compileOnly("gg.scala.commons:bukkit:4.0.2")

    compileOnly("gg.scala.cgs:parties:1.4.3")
    compileOnly("gg.scala.store:spigot:0.1.8")
    compileOnly("gg.scala.staff:scala-scstaff-plugin:1.0.9")
    compileOnly("gg.scala.friends:scala-friends:2.1.0")
    compileOnly("gg.scala.basics.plugin:scala-basics-plugin:1.0.7")
    compileOnly("gg.scala.achievements:scala-achievements-plugin:1.0.5")

    compileOnly("gg.scala.spigot:server:1.1.3")
    compileOnly("gg.scala.lemon:bukkit:1.8.1")
    compileOnly("gg.scala.cloudsync:spigot:1.0.1")

    compileOnly("gg.scala.practice.mi:lobby:1.3.2")
    compileOnly("gg.scala.practice.mi:game:1.3.2")
}

kapt {
    keepJavacAnnotationProcessors = true
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.fork()
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain(jdkVersion = 17)
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude(
            "**/*.kotlin_metadata",
            "**/*.kotlin_builtins"
        )

        archiveFileName.set(
            "scala-robots.jar"
        )
    }

    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.fork()
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions.javaParameters = true
        kotlinOptions.jvmTarget = "17"
    }

    getByName("build") {
        dependsOn(
            "shadowJar",
            "publishMavenJavaPublicationToScalaRepository"
        )
    }
}

publishing {
    repositories.configureScalaRepository(dev = currentBranch != "master")

    publications {
        register(
            name = "mavenJava",
            type = MavenPublication::class,
            configurationAction = shadow::component
        )
    }
}

fun RepositoryHandler.configureScalaRepository(dev: Boolean = false) {
    maven("${property("artifactory_contextUrl")}/gradle-${if (dev) "dev" else "release"}") {
        name = "scala"
        credentials {
            username = property("artifactory_user").toString()
            password = property("artifactory_password").toString()
        }
    }
}
