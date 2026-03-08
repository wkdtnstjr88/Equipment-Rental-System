package com.example.EquipmentRentalSystem.dto;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 추가 (안정성 확보)
public class EquipmentItemResponseDTO {
    private Long id;
    private String serialNumber;
    private String status;
    private String equipmentName;

    // 엔티티를 DTO로 변환하는 생성자
    public EquipmentItemResponseDTO(EquipmentItem item) {
        this.id = item.getId();
        this.serialNumber = item.getSerialNumber();
        this.status = item.getStatus();
        // 장비 모델명 가져오기 (equipment가 null인지 체크하면 더 완벽합니다)
        if (item.getEquipment() != null) {
            this.equipmentName = item.getEquipment().getName();
        }
        // 부모(Equipment)가 없을 경우를 대비한 방어 코드
        if (item.getEquipment() != null) {
            this.equipmentName = item.getEquipment().getName();
        } else {
            this.equipmentName = "미지정 장비";
        }
    }
}