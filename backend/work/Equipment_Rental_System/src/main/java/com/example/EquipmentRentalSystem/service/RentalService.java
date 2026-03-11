package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.dto.RentalRequestDTO;
import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import com.example.EquipmentRentalSystem.entity.RentalHistory;
import com.example.EquipmentRentalSystem.exception.RentalException;
import com.example.EquipmentRentalSystem.repository.EquipmentItemRepository;
import com.example.EquipmentRentalSystem.repository.RentalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    /**
     * [대여 이력 페이징 조회]
     * 전체 조회와 동적 검색을 하나의 메서드로 처리합니다.
     */
    @Transactional(readOnly = true)
    public Page<RentalHistoryResponseDTO> getRentalHistories(String searchType, String keyword, Pageable pageable) {
        // Repository에서 엔티티 페이지를 가져옵니다.
        // (searchType이 없어도 Repository의 @Query에서 기본 처리가 됩니다.)
        Page<RentalHistory> historyPage = rentalHistoryRepository.findByDynamicSearch(searchType, keyword, pageable);

        // 가져온 엔티티들을 DTO로 변환하여 반환합니다.
        return historyPage.map(this::convertToDTO);
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
    public void createRental(RentalRequestDTO dto) {
        // 1. [무결성 체크] 과거 날짜 예약 금지 (10분 버퍼 제공)
        // 현재 시간보다 10분 전까지는 '현재'로 인정해줌으로써 네트워크 지연 등 오차 해결
        if (dto.getRentalDate().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new RentalException("이미 지난 시간으로는 예약할 수 없습니다.");
        }


        // 2. DTO에서 itemId 꺼내서 장비 조회
        EquipmentItem item = equipmentItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RentalException("해당 장비가 없습니다."));

        // 3. 이미 대여 중인지 체크
        if (!"AVAILABLE".equals(item.getStatus())) {
            throw new RentalException("현재 대여 가능한 상태가 아닙니다.");
        }

        // 5. 장비 상태 변경
        item.setStatus("RENTED");

        // 6. 이력 저장
        RentalHistory history = new RentalHistory();
        history.setEquipmentItem(item);
        history.setMemberName(dto.getMemberName());
        history.setRentalDate(dto.getRentalDate());
        history.setReturnDate(dto.getReturnDate()); // 반납 예정일도 함께 저장
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
                .orElseThrow(() -> new RentalException("해당 대여 기록을 찾을 수 없습니다. ID: " + historyId));

        // 2. 이미 반납된 경우를 대비한 방어 로직 (면접 포인트)
        if (RentalHistory.STATUS_RETURNED.equals(history.getHistoryStatus())) {
            throw new RentalException("이미 반납 처리가 완료된 항목입니다.");
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