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

    @Query("SELECT r FROM RentalHistory r " +
            "JOIN FETCH r.equipmentItem ei " +
            "JOIN FETCH ei.equipment e " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "  (:searchType = 'equipmentName' AND e.name LIKE %:keyword%) OR " +
            "  (:searchType = 'serialNumber' AND ei.serialNumber LIKE %:keyword%) OR " +
            "  (:searchType = 'memberName' AND r.memberName LIKE %:keyword%) OR " +
            "  (:searchType = 'status' AND ( " +
            "     (:keyword = '貸出中' AND r.historyStatus = 'RENTED') OR " +
            "     (:keyword = '返却完了' AND r.historyStatus = 'RETURNED') " +
            "  )) " +
            ")")

    Page<RentalHistory> findByDynamicSearch(@Param("searchType") String searchType,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}