package az.transfer.money.customer.dtos.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerRequest {

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotBlank
    String pin;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
}

