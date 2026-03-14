package com.example.EquipmentRentalSystem.controller;

import com.example.EquipmentRentalSystem.entity.Member;
import com.example.EquipmentRentalSystem.exception.MemberException;
import com.example.EquipmentRentalSystem.repository.MemberRepository;
import com.example.EquipmentRentalSystem.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

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

    @GetMapping("/edit")
    public String editMemberForm(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
                                 Model model) {
        if (loginMember == null) {
            return "redirect:/login";
        }
        // 현재 로그인된 회원의 최신 정보를 DB에서 가져와 모델에 담음
        Member member = memberService.findOne(loginMember.getId());
        model.addAttribute("member", member);
        return "members/editMemberForm";
    }

    @PostMapping("/edit")
    public String editMember(@ModelAttribute("member") Member updateParam,
                             @SessionAttribute(name = "loginMember", required = false) Member loginMember,
                             HttpServletRequest request,
                             RedirectAttributes ra) {
        if (loginMember == null) return "redirect:/login";

        try {
            // 1. 서비스 호출 (강력한 비밀번호 유효성 검사 포함)
            memberService.updateMember(loginMember.getId(), updateParam);

            // 2. 세션 정보 최신화 (상단 바 이름 등 반영)
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", memberService.findOne(loginMember.getId()));

            ra.addFlashAttribute("message", "会員情報が正常に修正されました。");
            return "redirect:/";

        } catch (MemberException e) {
            // GlobalExceptionHandler가 처리하지만,
            // 폼으로 직접 메시지를 보내고 싶을 때 사용
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }
}
