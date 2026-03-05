package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<ShiftEntity, Long> {
    List<ShiftEntity> findByStore_StoreNumber(Long storeNumber);
    List<ShiftEntity> findByStore_StoreNumberAndShiftDate(Long storeNumber, LocalDate shiftDate);
    List<ShiftEntity> findByStore_StoreNumberAndShiftDateBetween(Long storeNumber, LocalDate startDate, LocalDate endDate);
}
