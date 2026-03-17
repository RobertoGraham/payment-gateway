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

This is the domain layer at the centre of payment-gateway that all other modules depend on. It
requires zero runtime dependencies and is agnostic of any specific technology or framework. It
contains the:

- Domain model.
- Driving and driven ports.
- Domain services.

### [payment-gateway-adapter-web](payment-gateway-adapter-web)

This is the driving adapter RESTful API through which merchants can interact with
payment-gateway.

#### POST /payments

Connects merchants to the `ProcessPaymentUseCase` driving port, allowing them to process payments.

Request:

```http
POST /payments HTTP/1.1
Content-Type: application/json
Host: localhost:8090

{
  "cardNumber": "4242424242424242",
  "expiryMonth": 1,
  "expiryYear": 2030,
  "currency": "GBP",
  "amount": 1,
  "cvv": "123"
}
```

Responses:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": "00000000-0000-0000-0000-000000000000",
  "status": "Authorized",
  "last4Digits": "4242",
  "expiryMonth": 1,
  "expiryYear": 2030,
  "currency": "GBP",
  "amount": 1
}
```

```http
HTTP/1.1 400 Bad Request
Content-Type: application/problem+json

{
  "detail": "Rejected",
  "instance": "/payments",
  "status": 400,
  "title": "Bad Request"
}
```

```http
HTTP/1.1 422 Unprocessable Content
Content-Type: application/problem+json

{
  "detail": "Rejected",
  "instance": "/payments",
  "status": 422,
  "title": "Unprocessable Content"
}
```

```http
HTTP/1.1 502 Bad Gateway
Content-Type: application/problem+json

{
  "instance": "/payments",
  "status": 502,
  "title": "Bad Gateway"
}
```

#### GET /payment/{id}

Connects merchants to the `RetrievePaymentQuery` driving port, allowing them to retrieve payments.

### [payment-gateway-adapter-acquiring-bank](payment-gateway-adapter-acquiring-bank)

### [payment-gateway-adapter-payment-repository](payment-gateway-adapter-payment-repository)

### [payment-gateway-application](payment-gateway-application)
