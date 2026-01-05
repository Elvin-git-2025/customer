package az.transfer.money.customer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "customer.service")
@Getter
@Setter
public class CustomerServiceProperties {
    private String url;
}