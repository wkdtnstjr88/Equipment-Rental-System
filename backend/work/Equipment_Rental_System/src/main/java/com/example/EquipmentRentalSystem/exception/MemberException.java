package com.example.EquipmentRentalSystem.exception;

public class MemberException extends RuntimeException {
    // 회원 가입이나 로그인 관련 비즈니스 예외
    public MemberException(String message) {
        super(message);
    }
}
