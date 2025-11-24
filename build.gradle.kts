/*
 * SPDX-FileCopyrightText: 2025 Lucimber UG
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("java-library")
    // Style and formatting
    id("com.diffplug.spotless") version "8.1.0"
    // Publishing
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("signing")
}

group = "com.lucimber"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    // Javadoc and sources JARs are handled by the Vanniktech Maven Publish plugin
}

repositories {
    mavenCentral()
}

dependencies {
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Integration test dependencies
    testImplementation("org.bouncycastle:bcprov-jdk18on:1.82")
    testImplementation("org.springframework.security:spring-security-crypto:6.5.6")
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
                "Main-Class" to "com.lucimber.bcrypt.Usage",
                "API-Entry-Point" to "com.lucimber.bcrypt.BCryptService",
                "API-Documentation" to "com.lucimber.bcrypt.Usage",
                "Implementation-Title" to "BCrypt Java",
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Lucimber",
                "Built-By" to System.getProperty("user.name"),
                "Built-JDK" to System.getProperty("java.version"),
            ),
        )
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

// Publishing configuration
mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates("com.lucimber", "lucimber-bcrypt", "1.0.0")

    pom {
        name.set("Bcrypt")
        description.set("A zero-dependency, implementation of the BCrypt password hashing algorithm.")
        inceptionYear.set("2025")
        url.set("https://github.com/lucimber/bcrypt-java")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("lucimber")
                name.set("Lucimber UG")
                url.set("https://github.com/lucimber/")
                email.set("devdev@lucimber.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/lucimber/bcrypt-java.git")
            developerConnection.set("scm:git:ssh://github.com:lucimber/bcrypt-java.git")
            url.set("https://github.com/lucimber/bcrypt-java")
        }
    }
}

// Configure signing to use GPG
signing {
    useGpgCmd()
}
