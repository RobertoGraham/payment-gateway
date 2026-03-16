plugins {
  `java-conventions`
  `java-library`
}

dependencies {
  compileOnly("org.projectlombok:lombok:1.18.44")
  annotationProcessor("org.projectlombok:lombok:1.18.44")
  testImplementation("org.assertj:assertj-core:3.27.7")
  testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
}
