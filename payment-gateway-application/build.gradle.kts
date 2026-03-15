plugins {
  `spring-boot-conventions`
  application
  id("org.springframework.boot")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webmvc")
  implementation("org.springframework.boot:spring-boot-starter-restclient")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
