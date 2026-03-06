package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.entity.RentalHistory;
import com.example.EquipmentRentalSystem.repository.RentalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalHistoryRepository rentalHistoryRepository;

    @Transactional(readOnly = true)
    public List<RentalHistoryResponseDTO> getAllRentalHistories() {
        return rentalHistoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RentalHistoryResponseDTO convertToDTO(RentalHistory history) {
        // 날짜 포맷팅 (2026-03-07 15:30)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String formattedRentalDate = history.getRentalDate().format(formatter);

        // 반납일이 없으면 "-"로 표시 (삼항 연산자 활용)
        String formattedReturnDate = (history.getReturnDate() != null)
                ? history.getReturnDate().format(formatter) : "-";

        // 우리가 정한 '이력 상태' 변수명!
        String historyStatus = switch (history.getStatus()) {
            case RentalHistory.STATUS_RENTED -> "대여 중";
            case RentalHistory.STATUS_RETURNED -> "반납 완료";
            default -> "알 수 없음";
        };

        // DTO 필드명도 historyStatus로 맞춘 바구니에 담기
        return new RentalHistoryResponseDTO(
                history.getId(),
                history.getEquipmentItem().getEquipment().getName(),
                history.getEquipmentItem().getSerialNumber(),
                history.getMemberName(),
                formattedRentalDate,
                formattedReturnDate,
                historyStatus
        );
    }
}