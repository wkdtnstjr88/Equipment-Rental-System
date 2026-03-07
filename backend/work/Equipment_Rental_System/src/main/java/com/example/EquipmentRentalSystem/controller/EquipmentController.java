package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.EquipmentResponseDTO;
import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.service.EquipmentService; // 서비스 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EquipmentController {

    // Repository 대신 Service를 주입받습니다.
    private final EquipmentService equipmentService;

    /**
     * 장비 목록 페이지 (재고 수량 포함)
     */
    @GetMapping("/equipments")
    public String list(Model model) {
        // 1. 서비스에서 재고 정보가 포함된 DTO 리스트를 가져옵니다.
        List<EquipmentResponseDTO> equipments = equipmentService.getAllEquipmentsWithStock();

        // 2. HTML(타임리프)에서 사용할 수 있도록 "equipments"라는 이름으로 담습니다.
        model.addAttribute("equipments", equipments);

        // 3. resources/templates/equipmentList.html 파일을 찾아 엽니다.
        return "equipmentList";
    }


}