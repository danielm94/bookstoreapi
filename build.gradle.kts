plugins {
    id("java")
}

group = "com.github.danielm94"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("com.h2database:h2:2.2.224")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor ("org.projectlombok:lombok:1.18.30")

    implementation("com.google.flogger:flogger-system-backend:0.8")
    implementation("com.google.flogger:flogger:0.8")

    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("commons-io:commons-io:2.15.1")


    implementation("com.mysql:mysql-connector-j:8.2.0")

    implementation(project(":DatabaseConnectionPool"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
}

tasks.test {
    useJUnitPlatform()

    outputs.upToDateWhen { false }
}
