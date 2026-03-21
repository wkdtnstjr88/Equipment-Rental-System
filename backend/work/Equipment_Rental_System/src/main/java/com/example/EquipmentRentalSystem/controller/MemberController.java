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

    @GetMapping("/add")
    public String addForm() {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute Member member,
                       @RequestParam String passwordConfirm,
                       HttpServletRequest request,
                       Model model) {

        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$";

        try {
            if (!member.getPassword().matches(passwordPattern)) {
                throw new MemberException("パスワードは英文字、数字、記号を組み合わせて8〜16文字で入力してください。");
            }

            if(!member.getPassword().equals(passwordConfirm)) {
                throw new MemberException("パスワードが一致しません。");
            }

            memberService.join(member);

            HttpSession session = request.getSession();
            session.setAttribute("loginMember", member);

            return "redirect:/";

        } catch (MemberException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("member", member);
            return "members/addMemberForm";
        }
    }

    @GetMapping("/edit")
    public String editMemberForm(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
                                 Model model) {
        if (loginMember == null) {
            return "redirect:/login";
        }
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
            memberService.updateMember(loginMember.getId(), updateParam);

            HttpSession session = request.getSession();
            session.setAttribute("loginMember", memberService.findOne(loginMember.getId()));

            ra.addFlashAttribute("message", "会員情報が正常に修正されました。");
            return "redirect:/";

        } catch (MemberException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/members/edit";
        }
    }
}
