package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import com.example.EquipmentRentalSystem.entity.RentalHistory;
import com.example.EquipmentRentalSystem.repository.EquipmentItemRepository;
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
    private final EquipmentItemRepository equipmentItemRepository;

    @Transactional(readOnly = true)
    public List<RentalHistoryResponseDTO> getAllRentalHistories() {
        return rentalHistoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RentalHistoryResponseDTO convertToDTO(RentalHistory history) {
        // 1. 날짜 가공
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String rentalDateStr = history.getRentalDate() != null ? history.getRentalDate().format(formatter) : "-";
        String returnDateStr = history.getReturnDate() != null ? history.getReturnDate().format(formatter) : "-";

        // 2. 장비(Equipment) 정보 가져오기
        Equipment equipment = history.getEquipmentItem().getEquipment();

        // 3. [수정됨] Repository를 사용하여 해당 모델의 재고가 있는지 실시간 체크
        // 엔티티 간의 관계를 거치지 않고 직접 DB에 물어보는 가장 안전한 방식입니다.
        boolean isAvailable = equipmentItemRepository.existsByEquipmentIdAndStatus(equipment.getId(), "AVAILABLE");

        // 4. 상태 한글 변환
        String statusDisplay = RentalHistory.STATUS_RENTED.equals(history.getHistoryStatus()) ? "대여 중" : "반납 완료";

        // 5. DTO 생성 (업데이트된 DTO 필드 순서에 맞춤)
        return new RentalHistoryResponseDTO(
                history.getId(),
                equipment.getName(),
                history.getEquipmentItem().getSerialNumber(),
                history.getMemberName(),
                rentalDateStr,
                returnDateStr,
                statusDisplay,
                history.getEquipmentItem().getId(),
                isAvailable,         // isModelAvailable
                equipment.getId()    // equipmentId
        );
    }

    @Transactional
    public void createRental(Long itemId, String memberName) {
        EquipmentItem item = equipmentItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다."));

        item.setStatus("RENTED");

        RentalHistory history = new RentalHistory();
        history.setEquipmentItem(item);
        history.setMemberName(memberName);
        history.setRentalDate(java.time.LocalDateTime.now());
        history.setHistoryStatus(RentalHistory.STATUS_RENTED);

        rentalHistoryRepository.save(history);
    }
}