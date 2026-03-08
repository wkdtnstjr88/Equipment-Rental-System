package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
}