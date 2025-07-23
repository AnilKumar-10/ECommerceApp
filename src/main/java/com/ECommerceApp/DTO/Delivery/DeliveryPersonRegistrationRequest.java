package com.ECommerceApp.DTO.Delivery;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeliveryPersonRegistrationRequest {
    @NotBlank(message = "ID is required")
    private String id; // Optional: can be null during creation

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian number")
    private String phone;

    @NotNull(message = "Role must not be null")
    @Size(min = 1, message = "At least one role is required")
    private String[] roles ;

    @NotNull(message = "Assigned areas must not be null")
    @Size(min = 1, message = "At least one assigned area is required")
    private List<@NotBlank(message = "Area name cannot be blank") String> assignedAreas;

}

