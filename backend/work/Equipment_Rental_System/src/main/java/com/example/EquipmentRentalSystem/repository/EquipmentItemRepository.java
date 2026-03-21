package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, Long> {

    boolean existsByEquipmentIdAndStatus(Long equipmentId, String status);

    long countByEquipmentIdAndStatus(Long equipmentId, String status);

    long countByEquipmentId(Long equipmentId);

    List<EquipmentItem> findByStatus(String status);

    List<EquipmentItem> findByEquipmentIdAndStatus(Long equipmentId, String status);

    @Query("SELECT ei FROM EquipmentItem ei WHERE " +
            "(:name IS NULL OR ei.equipment.name LIKE %:name%) AND " +
            "(:status IS NULL OR ei.status = :status)")
    List<EquipmentItem> findByFilters(@Param("name") String name, @Param("status") String status);
}