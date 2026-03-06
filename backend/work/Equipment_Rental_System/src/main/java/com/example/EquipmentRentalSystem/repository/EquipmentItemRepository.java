package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, Long> {
    boolean existsByEquipmentIdAndStatus(Long equipmentId, String status);
}