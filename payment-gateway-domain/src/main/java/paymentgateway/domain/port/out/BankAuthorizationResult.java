package paymentgateway.domain.port.out;

public sealed interface BankAuthorizationResult {

  record Authorized(String code) implements BankAuthorizationResult {

  }

  record Declined() implements BankAuthorizationResult {

  }

  record Failed() implements BankAuthorizationResult {

  }
}
