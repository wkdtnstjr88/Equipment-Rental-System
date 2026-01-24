package com.example.shiftmate.repository;

import com.example.shiftmate.entity.ShiftRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequestEntity, Long> {
    List<ShiftRequestEntity> findByShift_ShiftNumber(Long shiftNumber);
    List<ShiftRequestEntity> findByUser_UserNumber(Long userNumber);
    Optional<ShiftRequestEntity> findByShift_ShiftNumberAndUser_UserNumber(Long shiftNumber, Long userNumber);
    boolean existsByShift_ShiftNumberAndUser_UserNumber(Long shiftNumber, Long userNumber);
}
