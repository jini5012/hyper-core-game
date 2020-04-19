dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.patrick-mc:kotlin-utils:0.3-beta")
}

tasks {
    dokka {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokka"

        configuration {
            includeNonPublic = true
            jdkVersion = 8
        }
    }

    create<Jar>("dokkaJar") {
        archiveClassifier.set("javadoc")
        from(dokka)
        dependsOn(dokka)
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
}

try {
    publishing {
        publications {
            create<MavenPublication>("customEntity") {
                artifactId = if (project.name == "api") parent?.name else "${parent?.name}-${project.name}"
                from(components["java"])

                artifact(tasks["sourcesJar"])
                artifact(tasks["dokkaJar"])
                parent?.let { artifact(it.tasks["shadowJar"]) }

                repositories {
                    mavenLocal()

                    maven {
                        name = "central"

                        credentials {
                            username = project.property("centralUsername").toString()
                            password = project.property("centralPassword").toString()
                        }

                        val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                    }
                }

                pom {
                    name.set("custom-entity-bukkit")
                    description.set("A custom entity NMS library for bukkit server")
                    url.set("https://github.com/patrick-mc/custom-entity-bukkit")

                    licenses {
                        license {
                            name.set("GNU General Public License v2.0")
                            url.set("https://opensource.org/licenses/gpl-2.0.php")
                        }
                    }

                    developers {
                        developer {
                            id.set("patrick-mc")
                            name.set("PatrickKR")
                            email.set("mailpatrickkorea@gmail.com")
                            url.set("https://github.com/patrick-mc")
                            roles.addAll("developer")
                            timezone.set("Asia/Seoul")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/patrick-mc/custom-entity-bukkit.git")
                        developerConnection.set("scm:git:ssh://github.com:patrick-mc/custom-entity-bukkit.git")
                        url.set("https://github.com/patrick-mc/custom-entity-bukkit")
                    }
                }
            }
        }
    }

    signing {
        isRequired = true
        sign(tasks["jar"], tasks["sourcesJar"], tasks["dokkaJar"])
        sign(publishing.publications["customEntity"])
    }
} catch (e: groovy.lang.MissingPropertyException) {}