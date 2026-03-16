plugins {
  `spring-boot-conventions`
  id("org.springframework.boot")
}

dependencies {
  implementation(project(":payment-gateway-domain"))
  implementation("org.springframework.boot:spring-boot-autoconfigure")
  runtimeOnly(project(":payment-gateway-adapter-acquiring-bank"))
  runtimeOnly(project(":payment-gateway-adapter-payment-repository"))
  runtimeOnly(project(":payment-gateway-adapter-web"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
