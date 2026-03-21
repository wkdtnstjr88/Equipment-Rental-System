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

    @Transactional(readOnly = true)
    public Page<RentalHistoryResponseDTO> getRentalHistories(String searchType, String keyword, Pageable pageable) {
        Page<RentalHistory> historyPage = rentalHistoryRepository.findByDynamicSearch(searchType, keyword, pageable);

        return historyPage.map(this::convertToDTO);
    }

    private RentalHistoryResponseDTO convertToDTO(RentalHistory history) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String rentalDateStr = history.getRentalDate() != null ? history.getRentalDate().format(formatter) : "-";
        String returnDateStr = history.getReturnDate() != null ? history.getReturnDate().format(formatter) : "-";

        Equipment equipment = history.getEquipmentItem().getEquipment();
        boolean isAvailable = equipmentItemRepository.existsByEquipmentIdAndStatus(equipment.getId(), "AVAILABLE");
        String statusDisplay = RentalHistory.STATUS_RENTED.equals(history.getHistoryStatus()) ? "貸出中" : "返却完了";

        return new RentalHistoryResponseDTO(
                history.getId(),
                equipment.getName(),
                history.getEquipmentItem().getSerialNumber(),
                history.getMemberName(),
                rentalDateStr,
                returnDateStr,
                statusDisplay,
                history.getEquipmentItem().getId(),
                isAvailable,
                equipment.getId()
        );
    }

    @Transactional
    public void createRental(RentalRequestDTO dto) {
        if (dto.getRentalDate().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new RentalException("過去の日時で予約することはできません。");
        }


        EquipmentItem item = equipmentItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RentalException("該当する機器がありません"));

        // 3. 이미 대여 중인지 체크
        if (!"AVAILABLE".equals(item.getStatus())) {
            throw new RentalException("現在、貸出可能な状態ではありません。");
        }

        item.setStatus("RENTED");

        RentalHistory history = new RentalHistory();
        history.setEquipmentItem(item);
        history.setMemberName(dto.getMemberName());
        history.setRentalDate(dto.getRentalDate());
        history.setReturnDate(dto.getReturnDate());
        history.setHistoryStatus(RentalHistory.STATUS_RENTED);

        rentalHistoryRepository.save(history);
    }

    @Transactional
    public void returnRental(Long historyId) {
        RentalHistory history = rentalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RentalException("該当する貸出履歴が見つかりません。 ID: " + historyId));

        if (RentalHistory.STATUS_RETURNED.equals(history.getHistoryStatus())) {
            throw new RentalException("既に返却処理が完了している項目です。");
        }

        EquipmentItem item = history.getEquipmentItem();
        item.setStatus(EquipmentItem.STATUS_AVAILABLE);

        history.setReturnDate(LocalDateTime.now());
        history.setHistoryStatus(RentalHistory.STATUS_RETURNED);

    }


}