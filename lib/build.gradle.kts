plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testAnnotationProcessor(project(":lib", "archives"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    exclude("com/mammb/code/jsonstruct/Json_.class")
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "com.mammb"
version = "0.1.0"

val sonatypeUsername: String? by project
val sonatypePassword: String? by project

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "json-struct"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("json struct")
                description.set("json-struct is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object.")
                url.set("https://github.com/naotsugu/json-struct")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("naotsugu")
                        name.set("Naotsugu Kobayashi")
                        email.set("naotsugukobayashi@gmail.com")
                    }
                }
                scm {
                    connection.set("git@github.com:naotsugu/json-struct.git")
                    developerConnection.set("git@github.com:naotsugu/json-struct.git")
                    url.set("https://github.com/naotsugu/json-struct")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

