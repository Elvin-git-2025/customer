package az.transfer.money.customer.services;

import az.transfer.money.customer.dtos.requests.CreateCustomerRequest;
import az.transfer.money.customer.dtos.requests.UpdateCustomerRequest;
import az.transfer.money.customer.dtos.responses.CustomerResponse;
import az.transfer.money.customer.entities.Customer;
import az.transfer.money.customer.exceptions.CustomerNotFoundException;
import az.transfer.money.customer.mappers.CustomerMapper;
import az.transfer.money.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }
    
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return customerMapper.toResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerMapper.toResponseList(customerRepository.findAll());
    }

    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }

    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerMapper.updateCustomerFromRequest(request, customer);

        return customerMapper.toResponse(customerRepository.save(customer));
    }


    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }
}
