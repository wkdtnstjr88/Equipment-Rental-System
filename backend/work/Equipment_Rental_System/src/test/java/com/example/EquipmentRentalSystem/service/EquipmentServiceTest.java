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
    @DisplayName("整合性テスト：全5個のうち2個が「貸出中」の場合、在庫数は3であること")
    void calculateStock_Success() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("テスト用カメラ");

        when(equipmentRepository.findAll()).thenReturn(List.of(equipment));

        when(equipmentItemRepository.countByEquipmentId(1L)).thenReturn(5L);
        when(equipmentItemRepository.countByEquipmentIdAndStatus(1L, "AVAILABLE")).thenReturn(3L);

        List<EquipmentResponseDTO> result = equipmentService.getAllEquipmentsWithStock();

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getTotalCount(), "総数量が一致しません。");
        assertEquals(3, result.get(0).getAvailableCount(), "貸出可能数量が3ではありません。");
    }

    @Test
    @DisplayName("フィルタリングテスト：getAvailableItems呼び出し時、「AVAILABLE」状態のアイテムのみ返却されること")
    void filterAvailableItems_Success() {
        EquipmentItem item1 = new EquipmentItem();
        item1.setStatus("AVAILABLE");

        EquipmentItem item2 = new EquipmentItem();
        item2.setStatus("RENTED");

        when(equipmentItemRepository.findAll()).thenReturn(List.of(item1, item2));

        List<EquipmentItemResponseDTO> result = equipmentService.getAvailableItems();

        assertEquals(1, result.size(), "フィルタリングされたアイテムの個数が一致しません。");
        assertEquals("AVAILABLE", result.get(0).getStatus());
    }

    @Test
    @DisplayName("例外テスト：存在しない備品IDの照会時、「IllegalArgumentException」が発生すること")
    void findDetails_Fail_NotFound() {
        when(equipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            equipmentService.getItemDetails(999L);
        });
    }
}