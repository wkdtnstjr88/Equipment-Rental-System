package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.service.EquipmentService; // 서비스 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class EquipmentController {

    // Repository 대신 Service를 주입받습니다.
    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    public String list(Model model) {
        // 서비스에게 목록을 가져오라고 시킵니다.
        model.addAttribute("equipmentList", equipmentService.findAll());
        return "equipmentList";
    }


}