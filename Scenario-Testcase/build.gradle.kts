plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    implementation("org.seleniumhq.selenium:selenium-java:4.25.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.7.0")

}

tasks.test {
    useJUnitPlatform()
}