package az.transfer.money.util.service;


import az.transfer.money.customer.dtos.requests.CreateCustomerRequest;
import az.transfer.money.customer.dtos.responses.CustomerResponse;
import az.transfer.money.customer.entities.Customer;
import az.transfer.money.customer.exceptions.CustomerNotFoundException;
import az.transfer.money.customer.mappers.CustomerMapper;
import az.transfer.money.customer.repositories.CustomerRepository;
import az.transfer.money.customer.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;


@ExtendWith(SpringExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldReturnCustomerResponse_whenCustomerIsExits() {
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        Customer customer = new Customer();
        CustomerResponse customerResponse = new CustomerResponse();

        when(customerMapper.toEntity(createCustomerRequest)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = customerService.createCustomer(createCustomerRequest);


        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(customerResponse);

        verify(customerMapper).toEntity(createCustomerRequest);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void getCustomerById_shouldReturnCustomerResponse_whenCustomerIsExits() {
        Long customerId = 1L;
        Customer customer = new Customer();
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);
        CustomerResponse result = customerService.getCustomerById(customerId);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(customerResponse);
        verify(customerRepository).findById(customerId);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void getCustomerById_shouldThrowException_whenCustomerIsNotFound() {
        Long customerId = 99L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customerService.getCustomerById(customerId))
        .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found with id: " + customerId);
        verify(customerRepository).findById(customerId);
    }
}
