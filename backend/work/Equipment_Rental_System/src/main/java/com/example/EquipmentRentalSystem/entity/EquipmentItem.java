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

    private String serialNumber;
    private String status;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
}