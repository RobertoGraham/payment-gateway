plugins {
  `module-conventions`
  id("org.springframework.boot")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-autoconfigure")
  implementation(project(":payment-gateway-domain"))
  runtimeOnly(project(":payment-gateway-adapter-acquiring-bank"))
  runtimeOnly(project(":payment-gateway-adapter-payment-repository"))
  runtimeOnly(project(":payment-gateway-adapter-web"))
  runtimeOnly("org.springframework.boot:spring-boot-micrometer-tracing-opentelemetry")
  runtimeOnly("io.micrometer:micrometer-tracing-bridge-otel")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
