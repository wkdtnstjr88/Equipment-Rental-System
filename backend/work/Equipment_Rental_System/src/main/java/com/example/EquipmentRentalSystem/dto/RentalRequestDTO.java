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

    @NotNull(message = "機器を選択してください。")
    private Long itemId;
    @NotBlank(message = "借用者の氏名を入力してください。")
    private String memberName;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime rentalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime returnDate;
}