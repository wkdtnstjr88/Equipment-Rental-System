package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    List<StoreEntity> findByOwner_UserNumber(Long ownerUserNumber);
}