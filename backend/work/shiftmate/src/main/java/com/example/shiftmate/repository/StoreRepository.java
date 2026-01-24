package com.example.shiftmate.repository;

import com.example.shiftmate.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    List<StoreEntity> findByOwner_UserNumber(Long ownerUserNumber);
}
