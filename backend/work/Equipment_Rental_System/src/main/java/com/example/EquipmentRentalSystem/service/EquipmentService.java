package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용으로 성능을 최적화합니다.
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    // 모든 장비 목록 조회
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }


    // 나중에 여기에 '대여하기', '반납하기' 비즈니스 로직이 들어옵니다!
}