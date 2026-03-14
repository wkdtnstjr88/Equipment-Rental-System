package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.entity.Member;
import com.example.EquipmentRentalSystem.exception.MemberException;
import com.example.EquipmentRentalSystem.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/add")
    public String addForm() {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute Member member,
                       @RequestParam String passwordConfirm,
                       HttpServletRequest request) { // 👈 세션 생성을 위해 추가

        // 비밀번호 정규식 (영문, 숫자, 특수문자 포함 8~16자)
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$";

        if (!member.getPassword().matches(passwordPattern)) {
            throw new MemberException("비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다.");
        }

        // 1. 비밀번호 일치 확인
        if(!member.getPassword().equals(passwordConfirm)) {
            throw new MemberException("비밀번호가 일치하지 않습니다.");
        }

        // 2. DB 저장
        memberService.join(member);

        // 3. ✅ 회원가입 성공 시 바로 세션 생성 (자동 로그인)
        // 방금 가입한 member 객체를 세션에 저장합니다.
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", member);

        // 4. 메인 화면으로 이동
        return "redirect:/";
    }
}
