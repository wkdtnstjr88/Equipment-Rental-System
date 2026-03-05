package com.example.EquipmentRentalSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userNumber;
    //NotBlank의 message는 GlobalExceptionHandler.java 클래스 유효성 검사 코드 추가로 사용가능
    @NotBlank(message = "ユーザーIDは必須です。")
    private String userId;
    @NotBlank(message = "パスワードは必須です。")
    private String password;
    @NotBlank(message = "名前は必須です。")
    private String name;
    @NotBlank(message = "ユーザータイプは必須です。")
    private String userType;
}
