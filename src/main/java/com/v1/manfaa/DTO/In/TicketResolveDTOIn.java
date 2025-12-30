package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResolveDTOIn {
    @NotNull(message = "ticket must not be null")
    Integer ticketId;
    @NotBlank(message = "Body is required")
    @Size(min = 10, max = 1000, message = "Body must be between 10 and 1000 characters")
    private String body;

}
