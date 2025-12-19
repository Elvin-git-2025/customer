package az.kapitalbank.mb.bff.transfermobile.transfer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "customer-service",
        url = "${customer.service.url}"
)
public interface CustomerClient {

    @GetMapping("/api/v1/customer/{id}/exists")
    boolean existsById(@PathVariable Long id);
}
