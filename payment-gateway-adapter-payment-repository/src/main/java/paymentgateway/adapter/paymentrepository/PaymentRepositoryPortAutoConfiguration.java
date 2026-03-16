package paymentgateway.adapter.paymentrepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@Import(PaymentRepositoryAdapter.class)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AutoConfiguration
final class PaymentRepositoryPortAutoConfiguration {

}
