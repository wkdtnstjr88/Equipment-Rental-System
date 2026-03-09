package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.EquipmentResponseDTO; // DTO 임포트 필요
import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import com.example.EquipmentRentalSystem.repository.EquipmentItemRepository;
import com.example.EquipmentRentalSystem.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentItemRepository equipmentItemRepository;

    // [수정됨] 모든 장비를 DTO 리스트로 변환해서 가져오기
    public List<EquipmentResponseDTO> getAllEquipments() {
        return equipmentRepository.findAll().stream()
                .map(this::convertToDTO) // 아래 만든 변환 메서드 사용
                .collect(Collectors.toList());
    }

    private EquipmentResponseDTO convertToDTO(Equipment equipment) {
        // 1. 엔티티 내부 로직을 사용하거나, 리포지토리를 통해 계산된 값을 가져옵니다.
        long available = equipment.getAvailableCount();
        long total = equipment.getTotalCount(); // 아이템 리스트의 크기가 곧 전체 수량

        return new EquipmentResponseDTO(
                equipment.getId(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getDailyPrice(),
                available, // 대여 가능 수량
                total      // 전체 수량 (새로 추가!)
        );
    }

    public List<EquipmentItemResponseDTO> getItemDetails(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("장비 없음"));

        return equipment.getItems().stream()
                .map(EquipmentItemResponseDTO::new) // item 엔티티 하나를 넘겨서 DTO로 변환
                .collect(Collectors.toList());
    }

    public List<EquipmentItemResponseDTO> getAvailableItems() {
        // 모든 기기 아이템 중 상태가 "AVAILABLE"인 것만 필터링해서 DTO로 변환
        return equipmentItemRepository.findAll().stream()
                .filter(item -> "AVAILABLE".equals(item.getStatus()))
                // 🔥 직접 필드를 하나하나 넣지 말고, item 엔티티 통째로 넘기세요!
                .map(EquipmentItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 1. 특정 장비 모델에 속한 대여 가능 아이템만 가져오기
    public List<EquipmentItemResponseDTO> getAvailableItemsByEquipmentId(Long equipmentId) {
        return equipmentItemRepository.findByEquipmentIdAndStatus(equipmentId, EquipmentItem.STATUS_AVAILABLE)
                .stream()
                .map(item -> new EquipmentItemResponseDTO(item))
                .toList();
    }

    /**
     * 모든 장비 모델 중에서 대여 가능한 모든 아이템 가져오기
     */
    public List<EquipmentItemResponseDTO> getAllAvailableItems() {
        // Repository에서 상태가 "AVAILABLE"인 모든 기기를 가져옵니다.
        return equipmentItemRepository.findByStatus("AVAILABLE")
                .stream()
                .map(item -> new EquipmentItemResponseDTO(item))
                .toList();
    }

    public List<EquipmentResponseDTO> getAllEquipmentsWithStock() {
        // 1. 모든 장비(모델) 목록을 가져옵니다. (findAll은 JpaRepository 기본 메서드!)
        List<Equipment> equipments = equipmentRepository.findAll();

        // 2. 각 장비별로 재고 수량을 파악하여 DTO로 변환합니다.
        return equipments.stream().map(eq -> {
            // 대여 가능한 수량 (String "AVAILABLE" 사용)
            long available = equipmentItemRepository.countByEquipmentIdAndStatus(
                    eq.getId(), EquipmentItem.STATUS_AVAILABLE);

            // 해당 모델의 전체 아이템 수량
            long total = equipmentItemRepository.countByEquipmentId(eq.getId());

            // 3. 생성자 순서에 맞춰 DTO 생성 (id, name, category, dailyPrice, available, total)
            return new EquipmentResponseDTO(
                    eq.getId(),
                    eq.getName(),
                    eq.getCategory(),
                    eq.getDailyPrice(),
                    available,
                    total
            );
        }).toList();
    }

    public List<EquipmentItemResponseDTO> searchItems(String name, String status) {
        // 만약 상태값이 빈 문자열("")로 들어오면 null로 처리하여 쿼리 조건에서 제외되게 합니다.
        String filterStatus = (status != null && !status.isEmpty()) ? status : null;

        List<EquipmentItem> items = equipmentItemRepository.findByFilters(name, filterStatus);

        return items.stream()
                .map(EquipmentItemResponseDTO::new)
                .collect(Collectors.toList());
    }


}