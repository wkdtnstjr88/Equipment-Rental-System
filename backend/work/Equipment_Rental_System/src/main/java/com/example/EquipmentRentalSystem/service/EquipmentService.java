package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.EquipmentResponseDTO; // DTO 임포트 필요
import com.example.EquipmentRentalSystem.entity.Equipment;
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

    // 엔티티 -> DTO 변환 로직 (private으로 숨김)
    private EquipmentResponseDTO convertToDTO(Equipment equipment) {
        return new EquipmentResponseDTO(
                equipment.getId(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getDailyPrice(),
                equipment.getAvailableCount() // 엔티티 안에 이미 만든 for문 로직 호출!
        );
    }

    public List<EquipmentItemResponseDTO> getItemDetails(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("장비 없음"));

        return equipment.getItems().stream()
                .map(item -> new EquipmentItemResponseDTO(
                        item.getId(),
                        item.getSerialNumber(),
                        item.getStatus(),
                        equipment.getName() // 부모 엔티티의 이름을 가져와서 담아줌
                ))
                .collect(Collectors.toList());
    }

    public List<EquipmentItemResponseDTO> getAvailableItems() {
        // 모든 기기 아이템 중 상태가 "AVAILABLE"인 것만 필터링해서 DTO로 변환
        return equipmentItemRepository.findAll().stream()
                .filter(item -> "AVAILABLE".equals(item.getStatus()))
                .map(item -> new EquipmentItemResponseDTO(
                        item.getId(),
                        item.getSerialNumber(),
                        item.getStatus(),
                        item.getEquipment().getName()
                ))
                .collect(Collectors.toList());
    }
}