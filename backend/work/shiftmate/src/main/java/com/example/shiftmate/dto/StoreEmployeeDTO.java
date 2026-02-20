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
    //下記の2行追加しました（reason, adminComment)
    private String reason;
    private String adminComment;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
