package com.example.EquipmentRentalSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EquipmentResponseDTO {
    private Long id;
    private String name;
    private String category;
    private int dailyPrice;
    private long availableCount; // 엔티티의 getAvailableCount() 결과를 담습니다.
}