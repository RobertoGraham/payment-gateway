# Kevin Gracie Checkout.com Engineering assessment

## Changes to request DTO
- I've made all fields final and removed setters for the purposes of immutability
- Used wrapper types for numeric fields that are mandatory rather than primitives to allow for null checks
  - This is because Jackson will create an instance of the request before the validations are evaluated and primitive fields will be initialised to 0 and validation will pass

## Changes to exception handling
- Swapped `EventProcessingException` for a more specific `PaymentProcesingException` when a payment can't be found for an id

## Idempotency
I've implemented a very rudimentary idempotency check that checks the in memory data store for an object that contains
similar information to the incoming processedPayment request and if so considers it a replayed request. This is a grossly unreliable 
approach given the data as a card holder making two separate payments for the same amount would be considered a replayed request.

An improvement would be if we got a request timestamp from the merchant to at least identify when the payments were made so as to disambiguate them.

A better alternative would be for the merchant to provide an idempotency key with each processedPayment request which we could then use to identify replayed requests.

## Production readiness
There's a long way to go before this solution would be production ready. As we have an external service 
we are communicating with in the acquiring bank we should protect ourselves against any poor performance 
coming from this service. 

Circuit breakers, retry mechanisms and bulkheads would be helpful to protect this service.

### Monitoring and alerting
There is some very basic logging implemented in this solution. As this is a service that sits between 
a merchant and an acquiring bank we're lacking a correlation id tracking the calls between the parties 
but also between the different layers of this application.

Identifying activity by way of populating the last 4 numbers of a card could be problematic from an 
incident resolution perspective if there were to be multiple payments from the same card holder in 
quick succession.

In terms of alerting there is going to need to be some metrics pushed from this service to a 
monitoring system such as request times, number of authorized / declined payments. This would give us 
the ability to identify trends to see if performance is starting to degrade.

### Authentication / Authorization
As this is a service that deals with payments there should be adequate security to ensure that these APIs 
cannot be called by unauthenticated merchants. This could be achieved by the use of API keys such as a 
bearer token. 