package com.example.EquipmentRentalSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftDTO {
    private Long shiftNumber;
    private Long storeNumber;
    private LocalDate shiftDate;
    private Integer startTime;
    private Integer endTime;
    private Integer maxEmployees;
    private Integer currentEmployees;
}
