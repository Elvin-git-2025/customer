package az.transfer.money.util.service;


import az.transfer.money.customer.dtos.requests.CreateCustomerRequest;
import az.transfer.money.customer.dtos.requests.UpdateCustomerRequest;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


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

    @Test
    void getAllCustomers_shouldReturnCustomerResponse_whenCustomerIsExits() {

        Customer customer = new Customer();
        Customer customer1 = new Customer();

        CustomerResponse customerResponse = new CustomerResponse();
        CustomerResponse customerResponse1 = new CustomerResponse();

        List<CustomerResponse> customerResponseList = List.of(customerResponse, customerResponse1);
        List<Customer> customerList =List.of(customer, customer1);

        when(customerRepository.findAll()).thenReturn(customerList);
        when(customerMapper.toResponseList(customerList)).thenReturn(customerResponseList);

        List<CustomerResponse> result = customerService.getAllCustomers();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(customerResponseList);

        verify(customerRepository).findAll();
        verify(customerMapper).toResponseList(customerList);
     }

    @Test
    void updateCustomer_shouldReturnCustomerResponse_whenCustomerIsExits() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        CustomerResponse customerResponse = new CustomerResponse();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result=customerService.update(customerId, updateCustomerRequest);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(customerResponse);

        verify(customerRepository).findById(customerId);
        verify(customerMapper).updateCustomerFromRequest(updateCustomerRequest, customer);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);

    }

    @Test
    void updateCustomer_shouldThrowException_whenCustomerIsNotFound() {
        Long customerId = 99L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(customerId, new UpdateCustomerRequest()))
        .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found with id: " + customerId);
        verify(customerRepository).findById(customerId);
    }

    @Test
    void deleteCustomer_shouldDeleteCustomer_whenCustomerIsExits() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);

        customerService.delete(customerId);
        verify(customerRepository).existsById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void deleteCustomer_shouldThrowException_whenCustomerIsNotFound() {
        Long customerId = 99L;
        when(customerRepository.existsById(customerId)).thenReturn(false);
        assertThatThrownBy(() -> customerService.delete(customerId))
        .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found with id: " + customerId);
        verify(customerRepository).existsById(customerId);
        verify(customerRepository, never()).deleteById(customerId);
    }

    @Test
    void existsByIdCustomer_shouldReturnTrue_whenCustomerIsExits() {
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);


        boolean result = customerService.existsById(customerId);
        assertThat(result).isTrue();
        verify(customerRepository).existsById(customerId);
    }
}
