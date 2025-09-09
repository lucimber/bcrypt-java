plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.lucimber.crypto"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // Integration test dependencies
    testImplementation("org.bouncycastle:bcprov-jdk18on:1.77")
    testImplementation("org.springframework.security:spring-security-crypto:6.2.1")
    testImplementation("commons-logging:commons-logging:1.3.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}