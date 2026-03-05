package com.example.EquipmentRentalSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EquipmentItem {
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_RENTED = "RENTED";
    public static final String STATUS_BROKEN = "BROKEN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber; // 예: SN-001
    private String status;       // AVAILABLE, RENTED, BROKEN

    @ManyToOne
    @JoinColumn(name = "equipment_id") // DB 외래키 컬럼명
    private Equipment equipment;
}