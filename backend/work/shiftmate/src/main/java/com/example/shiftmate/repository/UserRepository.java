package com.example.shiftmate.repository;

import com.example.shiftmate.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// DB의 users 테이블(UserEntity)을 관리하는 인터페이스입니다.
// <UserEntity, Long>은 <관리할 엔티티, 그 엔티티의 PK 타입>을 의미합니다.
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 1. 로그인용: 아이디로 회원 정보 찾기
    // "SELECT * FROM users WHERE user_id = ?" 쿼리가 자동으로 실행됩니다.
    // (UserEntity가 없을 수도 있으니 Optional로 감싸는 것이 안전합니다)
    Optional<UserEntity> findByUserId(String userId);

    // 2. 회원가입용: 아이디 중복 확인
    // "SELECT count(*) FROM users WHERE user_id = ?" 와 비슷하게 존재 여부만 확인합니다.
    boolean existsByUserId(String userId);
}