package com.example.EquipmentRentalSystem.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RentalException.class)
    public String handleRentalException(RentalException e, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/rentals/history";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception e, Model model) {
        model.addAttribute("errorTitle", "システムエラー");
        model.addAttribute("errorMessage", "現在、サービスがご利用いただけません。しばらくしてからもう一度お試しください。");
        return "error-page";
    }

    @ExceptionHandler(MemberException.class)
    public String handleMemberException(MemberException e, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect/members/add";
    }
}