package com.example.EquipmentRentalSystem.dto;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EquipmentItemResponseDTO {
    // 1. [필드 영역] 변수들은 여기에 모여있어야 합니다.
    private Long id;
    private String serialNumber;
    private String status;
    private String equipmentName;
    private Long equipmentId; //

    // 2. [생성자 영역] 여기서는 값만 넣어줍니다.
    public EquipmentItemResponseDTO(EquipmentItem item) {
        this.id = item.getId();
        this.serialNumber = item.getSerialNumber();
        this.status = item.getStatus();

        if (item.getEquipment() != null) {
            this.equipmentName = item.getEquipment().getName();
            // 🔥 이제 이 변수를 정상적으로 인식합니다.
            this.equipmentId = item.getEquipment().getId();
        } else {
            this.equipmentName = "미지정 장비";
        }
    }
}