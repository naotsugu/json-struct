plugins {
    java
    id("me.champeau.jmh") version "0.6.8"
}

repositories {
    mavenCentral()
}

dependencies {
    jmh("com.google.code.gson:gson:2.10")
    jmh("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation(project(":lib", "archives"))
    annotationProcessor(project(":lib", "archives"))
}


tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}
