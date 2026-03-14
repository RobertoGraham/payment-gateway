plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.checkout"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

tasks.test {
    useJUnitPlatform()
}
