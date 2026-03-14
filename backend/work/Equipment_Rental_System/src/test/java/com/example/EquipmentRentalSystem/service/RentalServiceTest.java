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

@ExtendWith(MockitoExtension.class) // Mockito 기능을 사용하겠다고 선언
class RentalServiceTest {

    @InjectMocks
    private RentalService rentalService; // 테스트 대상 (Mock들을 주입받음)

    @Mock
    private RentalHistoryRepository rentalHistoryRepository; // 가짜 리포지토리

    @Mock
    private EquipmentItemRepository equipmentItemRepository; // 가짜 리포지토리

    @Test
    @DisplayName("성공: 모든 조건이 맞으면 대여가 성공하고 상태가 RENTED로 바뀐다")
    void createRental_Success() {
        // 1. Given: 테스트 환경 설정
        EquipmentItem item = new EquipmentItem();
        item.setId(1L);
        item.setStatus("AVAILABLE");

        RentalRequestDTO dto = new RentalRequestDTO();
        dto.setItemId(1L);
        dto.setRentalDate(LocalDateTime.now().plusHours(1));
        dto.setMemberName("테스터");

        // 가짜 객체(Mock)의 행동 정의
        when(equipmentItemRepository.findById(1L)).thenReturn(Optional.of(item));

        // 2. When: 실제 메서드 실행
        rentalService.createRental(dto);

        // 3. Then: 결과 검증
        assertEquals("RENTED", item.getStatus());
        verify(rentalHistoryRepository, times(1)).save(any(RentalHistory.class));
    }

    @Test
    @DisplayName("실패: 과거 시간으로 예약하면 RentalException이 발생한다")
    void createRental_Fail_PastDate() {
        // Given: 현재 시간보다 2시간 전으로 설정
        RentalRequestDTO dto = new RentalRequestDTO();
        dto.setRentalDate(LocalDateTime.now().minusHours(2));

        // When & Then: 예외가 발생하는지 검증
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.createRental(dto);
        });

        assertEquals("이미 지난 시간으로는 예약할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("실패: 이미 대여 중(RENTED)인 아이템은 대여할 수 없다")
    void createRental_Fail_AlreadyRented() {
        // Given: 상태가 RENTED인 아이템 준비
        EquipmentItem rentedItem = new EquipmentItem();
        rentedItem.setId(10L);
        rentedItem.setStatus(EquipmentItem.STATUS_RENTED);

        RentalRequestDTO dto = new RentalRequestDTO();
        dto.setItemId(10L);
        dto.setRentalDate(LocalDateTime.now().plusDays(1)); // 날짜는 정상

        when(equipmentItemRepository.findById(10L)).thenReturn(Optional.of(rentedItem));

        // When & Then
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.createRental(dto);
        });

        assertEquals("현재 대여 가능한 상태가 아닙니다.", exception.getMessage());


    }

    @Test
    @DisplayName("실패: 이미 반납 완료된 건을 또 반납하면 예외가 발생한다")
    void returnRental_Fail_AlreadyReturned() {
        // Given: 이미 반납 완료된 상태의 이력
        RentalHistory history = new RentalHistory();
        history.setHistoryStatus(RentalHistory.STATUS_RETURNED);

        when(rentalHistoryRepository.findById(11L)).thenReturn(Optional.of(history));

        // When & Then
        RentalException exception = assertThrows(RentalException.class, () -> {
            rentalService.returnRental(11L);
        });

        assertEquals("이미 반납 처리가 완료된 항목입니다.", exception.getMessage());
    }
}