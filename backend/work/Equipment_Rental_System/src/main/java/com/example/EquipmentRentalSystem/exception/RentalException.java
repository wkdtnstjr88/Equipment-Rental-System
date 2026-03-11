package com.example.EquipmentRentalSystem.exception;

// RuntimeException을 상속받아 우리가 원할 때 던질 수 있게 만듭니다.
public class RentalException extends RuntimeException {
    public RentalException(String message) {
        super(message);
    }
}