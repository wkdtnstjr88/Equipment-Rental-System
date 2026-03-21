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

    public List<EquipmentResponseDTO> getAllEquipments() {
        return equipmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EquipmentResponseDTO convertToDTO(Equipment equipment) {
        long available = equipment.getAvailableCount();
        long total = equipment.getTotalCount();

        return new EquipmentResponseDTO(
                equipment.getId(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getDailyPrice(),
                available,
                total
        );
    }

    public List<EquipmentItemResponseDTO> getItemDetails(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("機器がありません。"));

        return equipment.getItems().stream()
                .map(EquipmentItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<EquipmentItemResponseDTO> getAvailableItems() {
        return equipmentItemRepository.findAll().stream()
                .filter(item -> "AVAILABLE".equals(item.getStatus()))
                .map(EquipmentItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<EquipmentItemResponseDTO> getAvailableItemsByEquipmentId(Long equipmentId) {
        return equipmentItemRepository.findByEquipmentIdAndStatus(equipmentId, EquipmentItem.STATUS_AVAILABLE)
                .stream()
                .map(item -> new EquipmentItemResponseDTO(item))
                .toList();
    }

    public List<EquipmentItemResponseDTO> getAllAvailableItems() {
        return equipmentItemRepository.findByStatus("AVAILABLE")
                .stream()
                .map(item -> new EquipmentItemResponseDTO(item))
                .toList();
    }

    public List<EquipmentResponseDTO> getAllEquipmentsWithStock() {
        List<Equipment> equipments = equipmentRepository.findAll();

        return equipments.stream().map(eq -> {
            long available = equipmentItemRepository.countByEquipmentIdAndStatus(
                    eq.getId(), EquipmentItem.STATUS_AVAILABLE);

            long total = equipmentItemRepository.countByEquipmentId(eq.getId());

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
        String filterStatus = (status != null && !status.isEmpty()) ? status : null;

        List<EquipmentItem> items = equipmentItemRepository.findByFilters(name, filterStatus);

        return items.stream()
                .map(EquipmentItemResponseDTO::new)
                .collect(Collectors.toList());
    }


}