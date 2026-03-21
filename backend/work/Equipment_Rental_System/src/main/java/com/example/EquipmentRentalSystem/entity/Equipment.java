package com.example.EquipmentRentalSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private int dailyPrice;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    private List<EquipmentItem> items = new ArrayList<>();

    public long getAvailableCount() {
        if (this.items == null) {
            return 0;
        }

        long count = 0;
        for (EquipmentItem item : this.items) {
            if ("AVAILABLE".equals(item.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public long getTotalCount() {
        if (this.items == null) {
            return 0;
        }
        return this.items.size();
    }
}