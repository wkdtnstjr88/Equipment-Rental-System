package com.example.EquipmentRentalSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * [메인 홈 화면 주소]
     * 사용자가 http://localhost:8080 으로 접속했을 때의 처리입니다.
     */
    @GetMapping("/")
    public String index() {
        // templates/index.html 파일을 찾아서 보여줍니다.
        return "index";
    }
}