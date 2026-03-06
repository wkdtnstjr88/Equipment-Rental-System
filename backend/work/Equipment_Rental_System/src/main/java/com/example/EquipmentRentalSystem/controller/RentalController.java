package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.service.EquipmentService;
import com.example.EquipmentRentalSystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor // 생성자 주입으로 서비스를 가져옵니다.
public class RentalController {

    private final RentalService rentalService;
    private final EquipmentService equipmentService;

    // 대여 이력 페이지 접속 주소: http://localhost:8080/rentals/history
    @GetMapping("/rentals/history")
    public String rentalHistoryList(Model model) {

        // 1. 서비스에서 가공된 DTO 리스트를 싹 가져옵니다.
        List<RentalHistoryResponseDTO> histories = rentalService.getAllRentalHistories();

        // 2. "histories"라는 이름으로 HTML에 데이터를 전달합니다.
        model.addAttribute("histories", histories);

        // 3. templates/rental/historyList.html 파일을 보여줍니다.
        return "historyList";
    }

    // 1. 대여 신청 페이지 보여주기 (GET)
    @GetMapping("/rentals/new")
    public String rentalForm(Model model) {
        // 대여 가능한 아이템 리스트를 가져와서 모델에 담음
        List<EquipmentItemResponseDTO> availableItems = equipmentService.getAvailableItems();
        model.addAttribute("items", availableItems);
        return "rentalForm";
    }

    @PostMapping("/rentals/new")
    public String createRental(@RequestParam Long itemId, @RequestParam String memberName) {
        rentalService.createRental(itemId, memberName);
        // 저장이 끝나면 이력 페이지로 리다이렉트
        return "redirect:/rentals/history";
    }


}