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

I have designed payment-gateway to strictly adhere to the principles of domain-driven design and the
hexagonal architecture pattern.
