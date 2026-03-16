plugins {
  `module-conventions`
  `java-library`
}

dependencies {
  api(project(":payment-gateway-domain"))
  implementation("org.springframework.boot:spring-boot-autoconfigure")
  implementation("org.springframework.boot:spring-boot-starter-jackson")
  implementation("org.springframework.boot:spring-boot-starter-restclient")
  implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.1.1"))
  implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-framework-retry")
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.testcontainers:testcontainers-junit-jupiter")
}
