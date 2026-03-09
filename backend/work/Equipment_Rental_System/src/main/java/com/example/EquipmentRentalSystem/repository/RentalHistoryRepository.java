package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.RentalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {
    // 기본 CRUD 메서드 자동 생성

    @Query("SELECT r FROM RentalHistory r " +
            "JOIN FETCH r.equipmentItem ei " + // 페이징 시 성능을 위해 FETCH JOIN 추천
            "JOIN FETCH ei.equipment e " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "  (:searchType = 'equipmentName' AND e.name LIKE %:keyword%) OR " +
            "  (:searchType = 'serialNumber' AND ei.serialNumber LIKE %:keyword%) OR " +
            "  (:searchType = 'memberName' AND r.memberName LIKE %:keyword%) OR " +
            "  (:searchType = 'status' AND ( " +
            "     (:keyword = '대여 중' AND r.historyStatus = 'RENTED') OR " +
            "     (:keyword = '반납 완료' AND r.historyStatus = 'RETURNED') " +
            "  )) " +
            ")")
        // 반환 타입을 Page로 변경하고 Pageable 인자를 추가합니다.
    Page<RentalHistory> findByDynamicSearch(@Param("searchType") String searchType,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}