package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.service.EquipmentService;
import com.example.EquipmentRentalSystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        // 이 부분이 없으면 팝업을 눌렀을 때 "대여할 아이템이 없다"는 메시지가 뜹니다.
        List<EquipmentItemResponseDTO> availableItems = equipmentService.getAllAvailableItems();
        model.addAttribute("items", availableItems);

        // 3. templates/rental/historyList.html 파일을 보여줍니다.
        return "historyList";
    }

    @GetMapping("/rentals/new")
    public String newRentalForm(@RequestParam(value = "equipmentId", required = false) Long equipmentId, Model model) {
        List<EquipmentItemResponseDTO> availableItems;

        if (equipmentId != null) {
            // 🔥 특정 장비 ID가 넘어온 경우, 해당 모델의 대여 가능한 아이템만 가져옵니다.
            availableItems = equipmentService.getAvailableItemsByEquipmentId(equipmentId);
        } else {
            // ID가 없는 경우(직접 접속 등) 기존처럼 모든 대여 가능 아이템을 가져옵니다.
            availableItems = equipmentService.getAllAvailableItems();
        }

        model.addAttribute("items", availableItems);
        model.addAttribute("selectedItemId", equipmentId);
        return "rentalForm"; // 대여 신청 페이지 이름
    }

    @PostMapping("/rentals/new")
    public String createRental(@RequestParam Long itemId, @RequestParam String memberName) {
        rentalService.createRental(itemId, memberName);
        // 저장이 끝나면 이력 페이지로 리다이렉트
        return "redirect:/rentals/history";
    }

    @PostMapping("/rentals/return/{id}")
    public String returnRental(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rentalService.returnRental(id); // 서비스 호출
            redirectAttributes.addFlashAttribute("message", "정상적으로 반납되었습니다."); //
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "반납 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 반납 후 다시 이력 목록 페이지로 이동합니다.
        return "redirect:/rentals/history";
    }
}