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
public class StoreDTO {
    private Long storeNumber;
    private String storeName;
    private String storeAddress;
    private String category;
    private Long ownerUserNumber;
    private Boolean autoApprove;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
