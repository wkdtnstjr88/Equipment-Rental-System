package com.example.EquipmentRentalSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentalRequestDTO {

    @NotNull(message = "장비를 선택해주세요.")
    private Long itemId;
    @NotBlank(message = "대여자 성함을 입력해주세요.")
    private String memberName;

    // 💡 ISO 8601 형식(T 포함)을 명확히 지정하여 파싱 오류를 방지합니다.
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime rentalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime returnDate;
}