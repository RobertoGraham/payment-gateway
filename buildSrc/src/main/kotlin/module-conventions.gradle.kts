@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  java
  id("io.spring.dependency-management")
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

dependencyManagement {
  imports {
    mavenBom(SpringBootPlugin.BOM_COORDINATES)
  }
}

tasks.withType<Test> {
  testLogging {
    events("passed", "skipped", "failed")
    exceptionFormat = TestExceptionFormat.FULL
  }
}
