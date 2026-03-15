plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:4.0.3")
  implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.7")
}
