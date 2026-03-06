package com.example.EquipmentRentalSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RentalHistory {

    // 대여 이력 상태 상수 정의
    public static final String STATUS_RENTED = "RENTED";      // 대여 중
    public static final String STATUS_RETURNED = "RETURNED";  // 정상 반납됨

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_item_id")
    private EquipmentItem equipmentItem;

    private String memberName;

    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;

    private String status; // 위의 상수값들이 저장됨
}