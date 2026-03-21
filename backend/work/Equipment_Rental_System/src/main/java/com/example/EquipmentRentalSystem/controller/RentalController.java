package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.EquipmentItemResponseDTO;
import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.dto.RentalRequestDTO;
import com.example.EquipmentRentalSystem.entity.Member;
import com.example.EquipmentRentalSystem.service.EquipmentService;
import com.example.EquipmentRentalSystem.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final EquipmentService equipmentService;

    @GetMapping("/rentals/history")
    public String rentalHistoryList(
            @RequestParam(value = "searchType", required = false, defaultValue = "equipmentName") String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<RentalHistoryResponseDTO> historyPage = rentalService.getRentalHistories(searchType, keyword, pageable);

        model.addAttribute("historyList", historyPage.getContent());
        model.addAttribute("page", historyPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "historyList";
    }

    @GetMapping("/rentals/new")
    public String newRentalForm(@RequestParam(value = "equipmentId", required = false) Long equipmentId
                                            ,Model model) {
        List<EquipmentItemResponseDTO> availableItems;

        if (equipmentId != null) {
            availableItems = equipmentService.getAvailableItemsByEquipmentId(equipmentId);
        } else {
            availableItems = equipmentService.getAllAvailableItems();
        }

        model.addAttribute("items", availableItems);
        model.addAttribute("selectedItemId", equipmentId);
        return "rentalForm";
    }

    @PostMapping("/rentals/new")
    public String createRental(@Valid @ModelAttribute("rentalRequest") RentalRequestDTO dto,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("items", equipmentService.getAllAvailableItems());
            return "rentalForm";
        }

        rentalService.createRental(dto);
        return "redirect:/rentals/history";
    }

    @PostMapping("/rentals/return/{id}")
    public String returnRental(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rentalService.returnRental(id);
            redirectAttributes.addFlashAttribute("message", "正常に返却されました。"); //
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "返却処理中にエラーが発生しました。: " + e.getMessage());
        }

        return "redirect:/rentals/history";
    }
}