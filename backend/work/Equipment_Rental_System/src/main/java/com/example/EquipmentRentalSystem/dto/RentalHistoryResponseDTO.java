package com.example.EquipmentRentalSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RentalHistoryResponseDTO {
    private Long id;
    private String equipmentName; // Equipment 엔티티에서 가져올 이름
    private String serialNumber;  // EquipmentItem 엔티티에서 가져올 S/N
    private String memberName;
    private String rentalDate;    // "2026-03-07 14:00" 형태로 가공될 예정
    private String returnDate;    // 반납 전이면 "-" 표시 예정
    private String historyStatus;  // "대여 중", "반납 완료"으로 가공
}