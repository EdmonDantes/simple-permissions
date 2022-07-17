/*
 * Copyright (c) 2022. Ilia Loginov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    signing
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.0"
}

val releaseVersion: String by project

group = "io.github.edmondantes"
version = releaseVersion

java {
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven {
        name = "Sonatype_releases"
        url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
    }
    maven {
        name = "Sonatype_snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks {
    register<Jar>("dokkaJar") {
        from(dokkaHtml)
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<PublishToMavenRepository> {
        onlyIf {
            project.hasProperty("sonatypeUsername") &&
                    project.hasProperty("sonatypePassword")
        }
    }

    withType<Sign> {
        onlyIf {
            project.hasProperty("signingPrivateKey") &&
                    project.hasProperty("signingPassword")
        }
    }
    whenTaskAdded {
        if (name == "initializeSonatypeStagingRepository" &&
            !(project.hasProperty("sonatypeUsername") && project.hasProperty("sonatypePassword"))
        ) {
            enabled = false
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri((project.findProperty("publishRepositoryUrl") as String?).orEmpty().ifEmpty { "./build/repo" })

            val username = project.findProperty("sonatypeUsername") as String?
            val password = project.findProperty("sonatypePassword") as String?
            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("Simple permissions")
                description.set("Small library for easy managing permissions of your application")
                url.set("https://github.com/EdmonDantes/simple-permissions")
                developers {
                    developer {
                        name.set("Ilia Loginov")
                        email.set("dantes2104@gmail.com")
                        organization.set("github")
                        organizationUrl.set("https://www.github.com")
                    }
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/EdmonDantes/simple-permissions.git")
                    developerConnection.set("scm:git:ssh://github.com:EdmonDantes/simple-permissions.git")
                    url.set("https://github.com/EdmonDantes/simple-permissions/tree/master")
                }
            }

            groupId = "io.github.edmondantes"
            artifactId = project.name
            version = releaseVersion

            from(components["java"])
            artifact(tasks["dokkaJar"])
        }
    }
}

signing {
    val keyId = findProperty("signingKeyId") as String?
    val privateKey = findProperty("signingPrivateKey") as String?
    val password = findProperty("signingPassword") as String?

    useInMemoryPgpKeys(keyId, privateKey, password)
    sign(publishing.publications["maven"])
}