package com.example.shiftmate.repository;

import com.example.shiftmate.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
}