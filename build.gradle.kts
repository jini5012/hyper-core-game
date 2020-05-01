plugins {
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.71"
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.github.patrick-mc"
    version = "0.3-beta"

    repositories {
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://dl.bintray.com/kotlin/dokka")
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        if (project.name != "api") mavenLocal()
    }

    dependencies {
        if (project.name != "api") implementation(project(":api"))
    }

    if (project.name != "api") {
        tasks.forEach { task ->
            if (task.name != "clean") {
                task.onlyIf {
                    gradle.taskGraph.hasTask(":shadowJar")
                }
            }
        }
    }
}

dependencies {
    subprojects {
        implementation(this)
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("dist")
    }

    create<Copy>("distJar") {
        from(shadowJar)
        into("C:\\Users\\Guest1\\desktop\\paper\\plugins")
    }
}
