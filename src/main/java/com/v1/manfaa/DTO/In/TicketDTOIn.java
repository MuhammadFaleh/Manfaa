package com.v1.manfaa.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketDTOIn {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Body is required")
    @Size(min = 10, max = 1000, message = "Body must be between 10 and 1000 characters")
    private String body;

    @NotBlank(message = "Category is required")
    private String category;

}
