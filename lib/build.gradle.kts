plugins {
    `java-library`
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
