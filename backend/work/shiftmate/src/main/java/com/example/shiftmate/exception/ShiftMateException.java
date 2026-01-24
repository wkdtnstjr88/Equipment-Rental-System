package com.example.shiftmate.exception;

public class ShiftMateException extends RuntimeException {
    public ShiftMateException(String message) {
        super(message);
    }

    public ShiftMateException(String message, Throwable cause) {
        super(message, cause);
    }
}
