package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.EquipmentResponseDTO;
import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import com.example.EquipmentRentalSystem.repository.EquipmentItemRepository;
import com.example.EquipmentRentalSystem.repository.EquipmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @InjectMocks
    private EquipmentService equipmentService;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentItemRepository equipmentItemRepository;

    @Test
    @DisplayName("정합성 테스트: 전체 5개 중 2개가 RENTED일 때, available 수량은 3이어야 한다")
    void calculateStock_Success() {
        // Given
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("테스트 카메라");

        when(equipmentRepository.findAll()).thenReturn(List.of(equipment));

        // Mock 설정: 전체 5개, AVAILABLE 상태는 3개라고 가정
        when(equipmentItemRepository.countByEquipmentId(1L)).thenReturn(5L);
        when(equipmentItemRepository.countByEquipmentIdAndStatus(1L, "AVAILABLE")).thenReturn(3L);

        // When
        List<EquipmentResponseDTO> result = equipmentService.getAllEquipmentsWithStock();

        // Then
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getTotalCount(), "전체 수량이 일치하지 않습니다.");
        assertEquals(3, result.get(0).getAvailableCount(), "대여 가능 수량이 3이 아닙니다.");
    }

    @Test
    @DisplayName("필터링 테스트: getAvailableItems 호출 시 AVAILABLE 상태인 아이템만 반환되어야 한다")
    void filterAvailableItems_Success() {
        // Given: AVAILABLE 1개, RENTED 1개 준비
        EquipmentItem item1 = new EquipmentItem();
        item1.setStatus("AVAILABLE");

        EquipmentItem item2 = new EquipmentItem();
        item2.setStatus("RENTED");

        when(equipmentItemRepository.findAll()).thenReturn(List.of(item1, item2));

        // When
        List<EquipmentItemResponseDTO> result = equipmentService.getAvailableItems();

        // Then
        assertEquals(1, result.size(), "필터링된 아이템 개수가 맞지 않습니다.");
        assertEquals("AVAILABLE", result.get(0).getStatus());
    }

    @Test
    @DisplayName("예외 테스트: 존재하지 않는 장비 ID 조회 시 IllegalArgumentException이 발생한다")
    void findDetails_Fail_NotFound() {
        // Given: 999번 ID는 존재하지 않음
        when(equipmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            equipmentService.getItemDetails(999L);
        });
    }
}