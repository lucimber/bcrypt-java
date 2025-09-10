plugins {
    id("java-library")
    id("maven-publish")
    id("com.diffplug.spotless") version "7.2.1"
}

group = "com.lucimber"
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
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Integration test dependencies
    testImplementation("org.bouncycastle:bcprov-jdk18on:1.81")
    testImplementation("org.springframework.security:spring-security-crypto:6.5.3")
    testImplementation("commons-logging:commons-logging:1.3.5")
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

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.lucimber.crypto.bcrypt.Usage",
                "API-Entry-Point" to "com.lucimber.crypto.bcrypt.BCryptService",
                "API-Documentation" to "com.lucimber.crypto.bcrypt.Usage",
                "Implementation-Title" to "BCrypt Java",
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Lucimber",
                "Built-By" to System.getProperty("user.name"),
                "Built-JDK" to System.getProperty("java.version"),
            ),
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "lucimber-bcrypt"
        }
    }
}

// Spotless configuration for code formatting
spotless {
    // Java formatting
    java {
        // Target all Java source files
        target("src/**/*.java")

        // License header
        licenseHeaderFile(rootProject.file("LICENSE.header"))

        // Remove unused imports
        removeUnusedImports()

        // Use Google Java Format
        googleJavaFormat("1.28.0").aosp().reflowLongStrings()

        // Import order
        importOrder(
            "",
            "java",
            "javax",
            "",
            "com.lucimber",
            "",
            "org",
            "com",
            "",
            "",
        )

        // Ensure files end with a newline
        endWithNewline()

        // Trim trailing whitespace
        trimTrailingWhitespace()
    }

    // Format build files
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
