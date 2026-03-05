package com.example.EquipmentRentalSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftRequestDTO {
    private Long requestNumber;
    private Long shiftNumber;
    private Long userNumber;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime processedAt;
}
