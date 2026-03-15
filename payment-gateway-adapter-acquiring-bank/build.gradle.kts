@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  `java-library`
  id("org.springframework.boot") version "4.0.3"
  id("io.spring.dependency-management") version "1.1.7"
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
  api(project(":payment-gateway-domain"))
  implementation("org.springframework.boot:spring-boot-autoconfigure")
  implementation("org.springframework.boot:spring-boot-starter-jackson")
  implementation("org.springframework.boot:spring-boot-starter-restclient")
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.testcontainers:testcontainers-junit-jupiter")
}

tasks.withType<Test> {
  testLogging {
    events("passed", "skipped", "failed")
    exceptionFormat = TestExceptionFormat.FULL
  }
}

tasks.bootJar {
  enabled = false
}
