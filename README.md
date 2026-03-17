# payment-gateway

**payment-gateway** is a domain-driven, modular application that enables merchants to accept online
payments through a RESTful API.

[![Build payment-gateway](https://github.com/RobertoGraham/payment-gateway/actions/workflows/build-payment-gateway.yaml/badge.svg)](https://github.com/RobertoGraham/payment-gateway/actions/workflows/build-payment-gateway.yaml)

To get started, you will need:

- [Docker](https://www.docker.com/products/docker-desktop) - to start the bank simulator.
- [JDK 25](https://bell-sw.com/pages/downloads/#jdk-25-lts) - to compile and run payment-gateway.

## Getting started

Start the bank simulator.

```shell
docker compose up --detach bank_simulator
```

Compile and run payment-gateway.

```shell
./gradlew bootRun
```

To visualize and interact with the API's resources, navigate
to http://localhost:8090/swagger-ui.html from a web browser.

## Architecture

I have built payment-gateway following the principles of domain-driven design and the hexagonal
architecture pattern. It is
a [multi-project Gradle build](https://docs.gradle.org/current/userguide/multi_project_builds.html)
comprised of 5 subprojects (modules), each representing a distinct layer: one domain module
containing all business logic and port interfaces, one application module acting as the composition
root, and three adapter modules implementing the driving (web) and driven (acquiring bank, payment
repository) sides of the hexagonal architecture.

### [payment-gateway-domain](payment-gateway-domain)

### [payment-gateway-adapter-web](payment-gateway-adapter-web)

### [payment-gateway-adapter-acquiring-bank](payment-gateway-adapter-acquiring-bank)

### [payment-gateway-adapter-payment-repository](payment-gateway-adapter-payment-repository)

### [payment-gateway-application](payment-gateway-application)
