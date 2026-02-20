package com.example.shiftmate.repository;

import com.example.shiftmate.entity.StoreEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployeeEntity, Long> {
    List<StoreEmployeeEntity> findByStore_StoreNumber(Long storeNumber);
    List<StoreEmployeeEntity> findByUser_UserNumber(Long userNumber);
    Optional<StoreEmployeeEntity> findByStore_StoreNumberAndUser_UserNumber(Long storeNumber, Long userNumber);
    boolean existsByStore_StoreNumberAndUser_UserNumber(Long storeNumber, Long userNumber);
    List<StoreEmployeeEntity> findByStore_StoreNumberAndStatus(Long storeNumber, String status);

}
