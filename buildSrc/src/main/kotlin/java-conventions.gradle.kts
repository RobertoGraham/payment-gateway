@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  java
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(25)
  }
}

testing {
  suites {
    withType<JvmTestSuite> {
      useJUnitJupiter()
      dependencies {
        runtimeOnly("org.junit.platform:junit-platform-launcher")
      }
    }
  }
}

repositories {
  mavenCentral()
}

tasks.withType<Test> {
  testLogging {
    events("passed", "skipped", "failed")
    exceptionFormat = TestExceptionFormat.FULL
  }
}
