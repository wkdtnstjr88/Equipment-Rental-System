package com.example.EquipmentRentalSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EquipmentItemResponseDTO {
    private Long id;
    private String serialNumber; // 기기 고유 번호
    private String status;       // AVAILABLE, RENTED 등
    private String equipmentName; // 어떤 장비 모델인지 (맥북 프로 등)
}