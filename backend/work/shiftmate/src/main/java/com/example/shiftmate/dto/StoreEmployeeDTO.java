package com.example.shiftmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEmployeeDTO {
    private Long relationNumber;
    private Long storeNumber;
    private Long userNumber;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
