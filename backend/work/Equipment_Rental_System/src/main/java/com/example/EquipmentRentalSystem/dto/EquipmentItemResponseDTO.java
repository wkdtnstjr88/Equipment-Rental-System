package com.example.EquipmentRentalSystem.dto;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EquipmentItemResponseDTO {
    private Long id;
    private String serialNumber; // 기기 고유 번호
    private String status;       // AVAILABLE, RENTED 등
    private String equipmentName; // 어떤 장비 모델인지 (맥북 프로 등)

    // 🔥 엔티티를 받아서 DTO로 변환해주는 생성자 추가
    public EquipmentItemResponseDTO(EquipmentItem item) {
        this.id = item.getId();
        this.serialNumber = item.getSerialNumber();
        this.status = item.getStatus();
        // 연관관계인 Equipment 엔티티에서 이름을 가져옵니다.
        this.equipmentName = item.getEquipment().getName();
    }
}