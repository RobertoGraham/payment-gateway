# payment-gateway

**payment-gateway** is a domain-driven, modular application that enables merchants to accept online
payments.

[![Build payment-gateway](https://github.com/RobertoGraham/payment-gateway/actions/workflows/build-payment-gateway.yaml/badge.svg)](https://github.com/RobertoGraham/payment-gateway/actions/workflows/build-payment-gateway.yaml)

To get started, you need:

- [JDK 25](https://bell-sw.com/pages/downloads/#jdk-25-lts) - to compile and run payment-gateway.
- [Docker](https://www.docker.com/products/docker-desktop) - to start the bank simulator.

## Getting started

Start the bank simulator.

```shell
docker compose up --detach bank_simulator
```

Compile and run payment-gateway.

```shell
./gradlew bootRun
```
