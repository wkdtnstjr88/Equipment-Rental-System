package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {
    // 기본 CRUD 메서드 자동 생성

    // 2. [추가 기능] 화면에 목록을 뿌려줄 때 최신순으로 정렬해서 가져오기 위해 정의합니다.
    List<RentalHistory> findAllByOrderByIdDesc();
}