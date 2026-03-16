plugins {
  `spring-boot-conventions`
  application
  id("org.springframework.boot")
}

dependencies {
  implementation(project(":payment-gateway-domain"))
  implementation("org.springframework.boot:spring-boot-starter-webmvc")
  implementation("org.springframework.boot:spring-boot-starter-restclient")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
  runtimeOnly(project(":payment-gateway-adapter-acquiring-bank"))
  runtimeOnly(project(":payment-gateway-adapter-payment-repository"))
  runtimeOnly(project(":payment-gateway-adapter-web"))
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
