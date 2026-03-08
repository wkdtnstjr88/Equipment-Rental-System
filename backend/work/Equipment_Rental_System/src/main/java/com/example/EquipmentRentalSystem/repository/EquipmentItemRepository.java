package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, Long> {

    boolean existsByEquipmentIdAndStatus(Long equipmentId, String status);

    // 특정 모델의 특정 상태(예: "AVAILABLE")인 아이템 개수 세기
    long countByEquipmentIdAndStatus(Long equipmentId, String status);

    // 특정 모델의 전체 아이템 개수 세기 (상태 무관)
    long countByEquipmentId(Long equipmentId);

    // 🔥 추가: 상태가 특정 값(예: "AVAILABLE")인 아이템 목록 조회
    List<EquipmentItem> findByStatus(String status);

    // 🔥 추가: 특정 모델 ID + 특정 상태인 아이템 목록 조회
    List<EquipmentItem> findByEquipmentIdAndStatus(Long equipmentId, String status);

    // 장비명 키워드 검색 및 상태 필터링 쿼리
    @Query("SELECT ei FROM EquipmentItem ei WHERE " +
            "(:name IS NULL OR ei.equipment.name LIKE %:name%) AND " +
            "(:status IS NULL OR ei.status = :status)")
    List<EquipmentItem> findByFilters(@Param("name") String name, @Param("status") String status);
}