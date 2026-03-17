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

The domain layer at the centre of payment-gateway that all other modules depend on. It requires zero
runtime dependencies and is agnostic of any specific technology or framework. It contains the:

- Domain model.
- Driving and driven ports.
- Domain services.

### [payment-gateway-adapter-web](payment-gateway-adapter-web)

The driving adapter RESTful API through which merchants can interact with payment-gateway.

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

Request:

```http
GET /payment/00000000-0000-0000-0000-000000000000 HTTP/1.1
Host: localhost:8090
```

Responses:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": "00000000-0000-0000-0000-000000000000",
  "status": "Declined",
  "last4Digits": "4242",
  "expiryMonth": 1,
  "expiryYear": 2030,
  "currency": "GBP",
  "amount": 1
}
```

```http
HTTP/1.1 404 Not Found
```

### [payment-gateway-adapter-acquiring-bank](payment-gateway-adapter-acquiring-bank)

The driven adapter that connects the domain layer to the acquiring bank's API by
implementing the`AcquiringBankPort` driven port. It ensures resilience by applying a combination of
retry and circuit breaker policies to acquiring bank payment authorization requests.

### [payment-gateway-adapter-payment-repository](payment-gateway-adapter-payment-repository)

The driven adapter that connects the domain layer to a payment repository by implementing the
`PaymentRepositoryPort` driven port. It allows the domain layer to persist and retrieve payment
information without being coupled to any specific database technology. I chose a `ConcurrentHashMap`
implementation to support safe, concurrent access by multiple threads.

### [payment-gateway-application](payment-gateway-application)

The composition root of payment-gateway where the domain and adapter modules are composed together
into a working application. Adapter modules are runtime-only dependencies, as they're effectively
Spring Boot starters that contain all the necessary configuration to be auto-discovered and
autoconfigured by Spring Boot.

## Improvements

- Tweak the retry and circuit breaker policies to match the acquiring bank's real-world behaviour
  and SLAs.
- Replace the current payment repository adapter with a more robust, production-ready implementation
  that connects to a real database.
- Improve the observability of the application by recording and exporting metrics such as
  authorization rate, decline volume, API response times, etc.
- Expand the suite of tests to include:
  - Merchant-centric acceptance tests.
  - Performance tests.
- API security, obviously:
  - RBAC to restrict access to authorized merchants.
  - Rate limiting to prevent abuse and ensure fair usage.
  - DDoS protection to prevent abuse and ensure availability.
