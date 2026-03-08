package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.EquipmentResponseDTO;
import com.example.EquipmentRentalSystem.entity.Equipment;
import com.example.EquipmentRentalSystem.entity.EquipmentItem;
import com.example.EquipmentRentalSystem.service.EquipmentService; // 서비스 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EquipmentController {

    // Repository 대신 Service를 주입받습니다.
    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    public String list(@RequestParam(required = false) String searchName,
                       @RequestParam(required = false) String status,
                       Model model) {

        // 변수 타입을 List<EquipmentItem>에서 List<EquipmentItemResponseDTO>로 변경합니다.
        List<EquipmentItemResponseDTO> items = equipmentService.searchItems(searchName, status);

        model.addAttribute("items", items);
        model.addAttribute("searchName", searchName);
        model.addAttribute("currentStatus", status);

        return "equipmentList";
    }


}