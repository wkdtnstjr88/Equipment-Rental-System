package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.entity.Member;
import com.example.EquipmentRentalSystem.exception.MemberException;
import com.example.EquipmentRentalSystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m->{
                    throw new MemberException("이미 사용 중인 아이디입니다.");
                });
    }

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    @Transactional
    public void updateMember(Long memberId, Member updateParam) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("該当하는 회원 정보가 없습니다."));

        // 1. 이메일, 이름, 주소 업데이트
        findMember.setEmail(updateParam.getEmail());
        findMember.setName(updateParam.getName());
        findMember.setAddress(updateParam.getAddress());

        // 2. 비밀번호 유효성 검사 (입력값이 있을 때만 실행)
        String password = updateParam.getPassword();
        if (password != null && !password.isEmpty()) {

            // 정규표현식: 영어(대소문자), 숫자, 특수문자 포함 8~20자
            String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";

            if (!Pattern.matches(passwordPattern, password)) {
                // 일본어 취업 대비용 메시지
                throw new MemberException("パスワードは8~20字で、英字、数字、特殊文字(@$!%*#?&)를 모두 포함해야 합니다.");
            }

            findMember.setPassword(password);
        }
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("該当する会員が見つかりません。"));
    }
}
