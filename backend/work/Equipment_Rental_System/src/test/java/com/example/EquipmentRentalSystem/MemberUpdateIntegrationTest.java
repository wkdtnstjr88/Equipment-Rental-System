package com.example.EquipmentRentalSystem;

import com.example.EquipmentRentalSystem.entity.Member;
import com.example.EquipmentRentalSystem.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberUpdateIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberService memberService;

    @Test
    @DisplayName("正常系テスト：ログインユーザーが適切な値で修正を要求した場合、更新に成功すること")
    void updateSuccessTest() throws Exception {
        Member member = new Member();
        member.setLoginId("user123");
        member.setPassword("oldPass123!");
        member.setName("山田 太郎");
        member.setEmail("old@test.com");
        memberService.join(member);

        mockMvc.perform(post("/members/edit")
                        .sessionAttr("loginMember", member)
                        .param("name", "田中 太郎")
                        .param("email", "new@test.com")
                        .param("address", "住所：東京都千代田区永田町1-7-1")
                        .param("password", "newPass123!"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("message"));

        Member updatedMember = memberService.findOne(member.getId());
        assertThat(updatedMember.getName()).isEqualTo("田中 太郎");
        assertThat(updatedMember.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("異常系テスト：パスワード規則違反時、修正に失敗し、入力画面へリダイレクトされること")
    void updateFailTest() throws Exception {
        Member member = new Member();
        member.setLoginId("failUser");
        member.setPassword("valid123!");
        memberService.join(member);

        mockMvc.perform(post("/members/edit")
                        .sessionAttr("loginMember", member)
                        .param("name", "異常系テスター")
                        .param("password", "12345"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/edit"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}