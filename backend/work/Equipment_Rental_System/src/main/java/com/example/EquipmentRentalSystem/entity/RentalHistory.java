package com.example.EquipmentRentalSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RentalHistory {

    public static final String STATUS_RENTED = "RENTED";
    public static final String STATUS_RETURNED = "RETURNED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_item_id")
    private EquipmentItem equipmentItem;

    private String memberName;

    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;

    private String historyStatus;
}