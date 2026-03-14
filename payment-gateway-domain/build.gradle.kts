@file:Suppress("UnstableApiUsage")

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
    }
  }
}
