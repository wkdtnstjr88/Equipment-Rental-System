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

import java.time.LocalDateTime;
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

    @Transactional(readOnly = true)
    public List<RentalHistoryResponseDTO> getDynamicSearchHistories(String searchType, String keyword) {
        // 검색 타입이 없으면 기본값 설정 또는 전체 조회
        if (searchType == null || searchType.isEmpty()) {
            return getAllRentalHistories();
        }

        return rentalHistoryRepository.findByDynamicSearch(searchType, keyword).stream()
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

        // 🔥 추가: 이미 대여 중인지 한 번 더 체크 (방어적 프로그래밍)
        if (!"AVAILABLE".equals(item.getStatus())) {
            throw new IllegalStateException("현재 대여 가능한 상태가 아닙니다.");
        }

        item.setStatus("RENTED");

        RentalHistory history = new RentalHistory();
        history.setEquipmentItem(item);
        history.setMemberName(memberName);
        history.setRentalDate(java.time.LocalDateTime.now());
        history.setHistoryStatus(RentalHistory.STATUS_RENTED);

        rentalHistoryRepository.save(history);
    }

    /**
     * [반납 실행]
     * 특정 이력 ID를 통해 대여 상태를 '반납 완료'로 변경합니다.
     */
    @Transactional // 데이터 변경이 일어나므로 트랜잭션 처리가 필수입니다.
    public void returnRental(Long historyId) {
        // 1. 특정 ID로 대여 이력을 조회합니다.
        RentalHistory history = rentalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대여 기록을 찾을 수 없습니다. ID: " + historyId));

        // 2. 이미 반납된 경우를 대비한 방어 로직 (면접 포인트)
        if (RentalHistory.STATUS_RETURNED.equals(history.getHistoryStatus())) {
            throw new IllegalStateException("이미 반납 처리가 완료된 항목입니다.");
        }

        // 3. 연결된 장비(Item)의 상태를 AVAILABLE로 변경합니다.
        EquipmentItem item = history.getEquipmentItem();
        item.setStatus(EquipmentItem.STATUS_AVAILABLE);

        // 4. 이력 정보에 반납일과 상태를 업데이트합니다.
        history.setReturnDate(LocalDateTime.now());
        history.setHistoryStatus(RentalHistory.STATUS_RETURNED);

        // 별도의 save() 호출 없이 JPA의 '더티 체킹'으로 DB에 반영됩니다.
    }


}