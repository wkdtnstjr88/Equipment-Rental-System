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
@AutoConfigureMockMvc // 컨트롤러를 가상으로 호출하기 위한 설정
@Transactional // 테스트 후 DB 롤백 (데이터 오염 방지)
public class MemberUpdateIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberService memberService;

    @Test
    @DisplayName("로그인한 사용자가 적절한 값으로 수정을 요청하면 성공해야 한다")
    void updateSuccessTest() throws Exception {
        // 1. Given: 기존 회원 가입 (테스트 데이터 준비)
        Member member = new Member();
        member.setLoginId("user123");
        member.setPassword("oldPass123!");
        member.setName("홍길동");
        member.setEmail("old@test.com");
        memberService.join(member);

        // 2. When: 수정 요청 시뮬레이션 (POST /members/edit)
        mockMvc.perform(post("/members/edit")
                        .sessionAttr("loginMember", member) // 세션에 로그인 정보 주입
                        .param("name", "김철수")          // 변경할 이름
                        .param("email", "new@test.com")   // 변경할 이메일
                        .param("address", "서울시 금천구")   // 변경할 주소
                        .param("password", "newPass123!")) // 새 비밀번호 규칙 준수

                // 3. Then: 결과 검증
                .andExpect(status().is3xxRedirection()) // 홈으로 리다이렉트 되는가?
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("message")); // 성공 메시지가 전달되었는가?

        // DB에 실제로 잘 반영되었는지 최종 확인
        Member updatedMember = memberService.findOne(member.getId());
        assertThat(updatedMember.getName()).isEqualTo("김철수");
        assertThat(updatedMember.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("비밀번호 규칙을 어기면 수정에 실패하고 다시 수정 페이지로 가야 한다")
    void updateFailTest() throws Exception {
        // Given: 회원 가입
        Member member = new Member();
        member.setLoginId("failUser");
        member.setPassword("valid123!");
        memberService.join(member);

        // When: 취약한 비밀번호(숫자만)로 수정을 시도할 때
        mockMvc.perform(post("/members/edit")
                        .sessionAttr("loginMember", member)
                        .param("name", "에러테스터")
                        .param("password", "12345")) // 규칙 위반! (MemberException 발생 지점)

                // Then: 리다이렉트 경로와 에러 메시지 확인
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/edit")) // 실패 시 다시 수정 폼으로 가는지
                .andExpect(flash().attributeExists("errorMessage"));
    }
}