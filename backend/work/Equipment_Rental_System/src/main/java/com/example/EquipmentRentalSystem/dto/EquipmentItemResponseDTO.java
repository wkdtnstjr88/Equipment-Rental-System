package com.example.EquipmentRentalSystem.dto;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EquipmentItemResponseDTO {
    private Long id;
    private String serialNumber;
    private String status;
    private String equipmentName;
    private Long equipmentId; //

    public EquipmentItemResponseDTO(EquipmentItem item) {
        this.id = item.getId();
        this.serialNumber = item.getSerialNumber();
        this.status = item.getStatus();

        if (item.getEquipment() != null) {
            this.equipmentName = item.getEquipment().getName();
            this.equipmentId = item.getEquipment().getId();
        } else {
            this.equipmentName = "未指定の機器";
        }
    }
}