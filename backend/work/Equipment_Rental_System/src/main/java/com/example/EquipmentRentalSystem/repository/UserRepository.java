package com.example.EquipmentRentalSystem.repository;

import com.example.EquipmentRentalSystem.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);

    // 타입별로 검색
    List<UserEntity> findByUserType(String userType);

    // 이름 검색
    List<UserEntity> findByNameContaining(String keyword);
    //아이디 조회
    List<UserEntity> findByUserIdContaining(String keyword);
}