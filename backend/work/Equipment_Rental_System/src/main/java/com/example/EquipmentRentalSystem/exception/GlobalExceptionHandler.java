package com.example.EquipmentRentalSystem.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 1. 대여 관련 예외 (비즈니스 로직)
    // "이력 목록"으로 되돌려 보내고, 상단에 Alert창만 띄워줌
    @ExceptionHandler(RentalException.class)
    public String handleRentalException(RentalException e, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", e.getMessage()); // 서비스에서 던진 메시지
        return "redirect:/rentals/history"; // 👈 목록 주소로 리다이렉트
    }

    // ✅ 2. 그 외 모든 예외 (시스템 오류)
    // 목록으로 가도 해결이 안 되는 상황이므로 "에러 전용 페이지"를 보여줌
    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception e, Model model) {
        model.addAttribute("errorTitle", "시스템 오류");
        model.addAttribute("errorMessage", "현재 서비스 이용이 어렵습니다. 잠시 후 다시 시도해 주세요.");
        return "error-page"; // 👈 templates/error-page.html 파일 실행
    }
}