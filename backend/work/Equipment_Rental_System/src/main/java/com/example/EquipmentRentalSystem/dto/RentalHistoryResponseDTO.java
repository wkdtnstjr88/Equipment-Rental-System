package com.example.EquipmentRentalSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RentalHistoryResponseDTO {
    private Long id;
    private String equipmentName;
    private String serialNumber;
    private String memberName;
    private String rentalDate;
    private String returnDate;
    private String historyStatus;
    private Long equipmentItemId;
    private boolean isModelAvailable;
    private Long equipmentId;

}