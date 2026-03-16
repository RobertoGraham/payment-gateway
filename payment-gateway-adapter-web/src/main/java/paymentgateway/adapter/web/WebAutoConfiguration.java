package paymentgateway.adapter.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(PaymentController.class)
@PropertySource("classpath:application-web.properties")
@AutoConfiguration(before = WebMvcAutoConfiguration.class)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WebAutoConfiguration {

}
