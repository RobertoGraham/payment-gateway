@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  `java-library`
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

dependencies {
  compileOnly("org.projectlombok:lombok:1.18.44")
  annotationProcessor("org.projectlombok:lombok:1.18.44")
  testImplementation("org.assertj:assertj-core:3.27.7")
}

tasks.withType<Test> {
  testLogging {
    events("passed", "skipped", "failed")
    exceptionFormat = TestExceptionFormat.FULL
  }
}
