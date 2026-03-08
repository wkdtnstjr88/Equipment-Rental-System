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
    public String rentalHistoryList(
            @RequestParam(value = "searchType", required = false, defaultValue = "equipmentName") String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<RentalHistoryResponseDTO> histories = rentalService.getDynamicSearchHistories(searchType, keyword);

        model.addAttribute("histories", histories);
        model.addAttribute("searchType", searchType); // 선택된 검색 타입 유지
        model.addAttribute("keyword", keyword);       // 입력된 키워드 유지

        // 모달용 데이터
        model.addAttribute("items", equipmentService.getAllAvailableItems());

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
    public String createRental(@RequestParam Long itemId, @RequestParam String memberName, RedirectAttributes reAttributes) {
        try {
            rentalService.createRental(itemId, memberName);
            // "message"라는 이름으로 성공 문구 전달
            reAttributes.addFlashAttribute("message", "대여가 완료되었습니다!");
        } catch (Exception e) {
            reAttributes.addFlashAttribute("errorMessage", "대여 중 오류가 발생했습니다.");
        }
        // 저장이 끝나면 이력 페이지로 리다이렉트
        return "redirect:/rentals/history";
    }

    @PostMapping("/rentals/return/{id}")
    public String returnRental(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rentalService.returnRental(id); // 서비스 호출
            redirectAttributes.addFlashAttribute("message", "정상적으로 반납되었습니다."); //
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "반납 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 반납 후 다시 이력 목록 페이지로 이동합니다.
        return "redirect:/rentals/history";
    }
}