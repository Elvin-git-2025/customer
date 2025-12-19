package az.kapitalbank.mb.bff.transfermobile.transfer.services;

import az.kapitalbank.mb.bff.transfermobile.customer.exceptions.CustomerNotFoundException;
import az.kapitalbank.mb.bff.transfermobile.transfer.client.CustomerClient;
import az.kapitalbank.mb.bff.transfermobile.transfer.dtos.requests.CreateTransferRequest;
import az.kapitalbank.mb.bff.transfermobile.transfer.dtos.responses.TransferResponse;
import az.kapitalbank.mb.bff.transfermobile.transfer.entities.Transfer;
import az.kapitalbank.mb.bff.transfermobile.transfer.enums.TransferStatus;
import az.kapitalbank.mb.bff.transfermobile.transfer.enums.TransferType;
import az.kapitalbank.mb.bff.transfermobile.transfer.exceptions.InvalidTransferException;
import az.kapitalbank.mb.bff.transfermobile.transfer.exceptions.TransferNotFoundException;
import az.kapitalbank.mb.bff.transfermobile.transfer.mappers.TransferMapper;
import az.kapitalbank.mb.bff.transfermobile.transfer.repositories.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final CustomerClient customerClient;

    public TransferResponse createTransfer(CreateTransferRequest request) {

        validate(request);

        Transfer transfer = transferMapper.convertToEntity(request);

        BigDecimal tariff = calculateTariff(request.getType());
        BigDecimal commission =
                calculateCommission(request.getAmount(), request.getType());

        transfer.setTariff(tariff);
        transfer.setCommission(commission);
        transfer.setStatus(TransferStatus.PENDING);
        transfer.setCreatedAt(LocalDateTime.now());

        Transfer savedTransfer = transferRepository.save(transfer);

        TransferResponse response =
                transferMapper.convertToResponse(savedTransfer);

        response.setTotalAmount(
                savedTransfer.getAmount()
                        .add(savedTransfer.getCommission())
                        .add(savedTransfer.getTariff())
        );

        return response;
    }

    private void validate(CreateTransferRequest request) {

        if (request.getCustomerId() == null) {
            throw new InvalidTransferException("Customer id is required");
        }

        validateCustomerExists(request.getCustomerId());

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Amount must be greater than zero");
        }

        if (request.getType() == null) {
            throw new InvalidTransferException("Transfer type is required");
        }

        if (request.getPayee() == null || request.getPayee().isBlank()) {
            throw new InvalidTransferException("Payee is required");
        }
    }
    private void validateCustomerExists(Long customerId) {
        try {
            boolean exists = customerClient.existsById(customerId);

            if (!exists) {
                throw new CustomerNotFoundException(customerId);
            }
        } catch (feign.FeignException ex) {
            throw new InvalidTransferException("Customer service unavailable");
        }
    }

    private BigDecimal calculateTariff(TransferType type) {
        return switch (type) {
            case CARD_TO_CARD -> new BigDecimal("1.00");
            case ACCOUNT_TO_CARD -> new BigDecimal("0.50");
        };
    }

    private BigDecimal calculateCommission(
            BigDecimal amount,
            TransferType type
    ) {
        BigDecimal rate = switch (type) {
            case CARD_TO_CARD -> new BigDecimal("0.02");
            case ACCOUNT_TO_CARD -> new BigDecimal("0.01");
        };

        return amount.multiply(rate);
    }

    public TransferResponse getTransferById(Long id) {

        if (id == null || id <= 0) {
            throw new InvalidTransferException(
                    "Transfer id must be positive"
            );
        }

        Transfer transfer =
                transferRepository.findById(id)
                        .orElseThrow(
                                () -> new TransferNotFoundException(id)
                        );

        return transferMapper.convertToResponse(transfer);
    }

    public List<TransferResponse> getTransfersByCustomerId(Long customerId) {

        if (customerId == null || customerId <= 0) {
            throw new InvalidTransferException(
                    "Customer id must be positive"
            );
        }

        List<Transfer> transfers =
                transferRepository.findAllByCustomerId(customerId);

        return transferMapper.convertToResponseList(transfers);
    }

    @Transactional
    public TransferResponse cancelTransfer(Long id) {

        if (id == null || id <= 0) {
            throw new InvalidTransferException(
                    "Transfer id must be positive"
            );
        }

        Transfer transfer =
                transferRepository.findById(id)
                        .orElseThrow(
                                () -> new TransferNotFoundException(id)
                        );

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new InvalidTransferException(
                    "Only PENDING transfers can be cancelled"
            );
        }

        transfer.setStatus(TransferStatus.CANCELLED);

        return transferMapper.convertToResponse(
                transferRepository.save(transfer)
        );
    }

    public TransferResponse updateTransfer(Long id, TransferStatus status) {

        if (id == null || id <= 0) {
            throw new InvalidTransferException(
                    "Transfer id must be positive"
            );
        }

        if (status == null) {
            throw new InvalidTransferException(
                    "Transfer status must not be null"
            );
        }

        Transfer transfer =
                transferRepository.findById(id)
                        .orElseThrow(
                                () -> new TransferNotFoundException(id)
                        );

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new InvalidTransferException(
                    "Only PENDING transfers can change status"
            );
        }

        transfer.setStatus(status);

        return transferMapper.convertToResponse(
                transferRepository.save(transfer)
        );
    }
}
