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

    // [수정된 부분] 스트림 대신 for문을 사용한 재고 확인 로직
    public long getAvailableCount() {
        if (this.items == null) {
            return 0;
        }

        long count = 0;
        // 리스트를 하나씩 꺼내서 확인합니다. (for-each 문)
        for (EquipmentItem item : this.items) {
            // 아이템의 상태가 "AVAILABLE"인 경우에만 카운트를 올립니다.
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
        return this.items.size(); // 리스트에 담긴 전체 개수 반환
    }
}