package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.dto.RentalHistoryResponseDTO;
import com.example.EquipmentRentalSystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor // 생성자 주입으로 서비스를 가져옵니다.
public class RentalController {

    private final RentalService rentalService;

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
}