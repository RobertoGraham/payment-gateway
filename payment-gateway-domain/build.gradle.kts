plugins {
  `module-conventions`
  `java-library`
}

dependencies {
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.assertj:assertj-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
}
