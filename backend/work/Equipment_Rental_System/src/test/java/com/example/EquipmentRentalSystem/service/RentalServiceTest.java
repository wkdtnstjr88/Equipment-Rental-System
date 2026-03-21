package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.dto.RentalRequestDTO;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import com.example.EquipmentRentalSystem.entity.RentalHistory;
import com.example.EquipmentRentalSystem.exception.RentalException;
import com.example.EquipmentRentalSystem.repository.EquipmentItemRepository;
import com.example.EquipmentRentalSystem.repository.RentalHistoryRepository;
import com.example.EquipmentRentalSystem.service.RentalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private EquipmentItemRepository equipmentItemRepository;

    @Test
    @DisplayName("正常系テスト：全条件を満たす場合、貸出が成功し、ステータスが「RENTED」に更新されること")
    void createRental_Success() {
        EquipmentItem item = new EquipmentItem();
        item.setId(1L);
        item.setStatus("AVAILABLE");

        RentalRequestDTO dto = new RentalRequestDTO();
        dto.setItemId(1L);
        dto.setRentalDate(LocalDateTime.now().plusHours(1));
        dto.setMemberName("テスター");

        when(equipmentItemRepository.findById(1L)).thenReturn(Optional.of(item));

        rentalService.createRental(dto);

        assertEquals("RENTED", item.getStatus());
        verify(rentalHistoryRepository, times(1)).save(any(RentalHistory.class));
    }

    @Test
    @DisplayName("異常系テスト：過去の日時で予約した場合、「RentalException」が発生すること")
    void createRental_Fail_PastDate() {
        RentalRequestDTO dto = new RentalRequestDTO();
        dto.setRentalDate(LocalDateTime.now().minusHours(2));

        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.createRental(dto);
        });

        assertEquals("過去の日時では予約できません。", exception.getMessage());
    }

    @Test
    @DisplayName("異常系テスト：既に「RENTED(貸出中)」状態のアイテムは貸出不可であること")
    void createRental_Fail_AlreadyRented() {
        EquipmentItem rentedItem = new EquipmentItem();
        rentedItem.setId(10L);
        rentedItem.setStatus(EquipmentItem.STATUS_RENTED);

        RentalRequestDTO dto = new RentalRequestDTO();
        dto.setItemId(10L);
        dto.setRentalDate(LocalDateTime.now().plusDays(1));

        when(equipmentItemRepository.findById(10L)).thenReturn(Optional.of(rentedItem));

        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.createRental(dto);
        });

        assertEquals("現在は貸出可能な状態ではありません。", exception.getMessage());


    }

    @Test
    @DisplayName("異常系テスト：既に「返却完了」済みの案件を再度返却した場合、例外が発生すること")
    void returnRental_Fail_AlreadyReturned() {
        RentalHistory history = new RentalHistory();
        history.setHistoryStatus(RentalHistory.STATUS_RETURNED);

        when(rentalHistoryRepository.findById(11L)).thenReturn(Optional.of(history));

        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.returnRental(11L);
        });

        assertEquals("既に返却処理が完了している項目です。", exception.getMessage());
    }
}