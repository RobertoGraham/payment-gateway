# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**





## Piotr Konopacki
I have completed the challenge, please read some information I need to pass along:

- I followed the TDD approach throughout the exercise
- I introduced the mapper which merges information received from the stub and the information received from request
- I used thorough Junit tests for the mapper
- I used an extended Junit integration tests
- I decided not to write mock services for Controller, Service, and Repository as they are tested using MockMvc tests due to time restrictions and good test coverage with MockMvc integration tests. They can always be added, but any change now should cause one of the tests to fail

### Outstanding Issues

Due to the time restrictions I had to stop investigating some issues and submit the code 24h prior to the interview
- I was not able to push the code to the GitHub, I send the gzipped tarball instead
- The Java Validation framework does not seem to be working, I haven't managed to understand why
- When the payment status is "Declined", I do not store the transaction in the list
- I was not able to reproduce the "Rejected" payment status, due to teh issue with Java Validation library
- introduced a few libraries (build.gradle)

