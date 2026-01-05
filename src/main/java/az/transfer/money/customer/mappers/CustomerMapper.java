package az.transfer.money.customer.mappers;

import az.transfer.money.customer.dtos.requests.CreateCustomerRequest;
import az.transfer.money.customer.dtos.requests.UpdateCustomerRequest;
import az.transfer.money.customer.dtos.responses.CustomerResponse;
import az.transfer.money.customer.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(
            target = "createdAt",
            expression =  "java(java.time.LocalDateTime.now())"
    )
    Customer toEntity(CreateCustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    void updateCustomerFromRequest(
            UpdateCustomerRequest request,
            @MappingTarget Customer customer
    );

    List<CustomerResponse> toResponseList(List<Customer> customers);
}
