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
        member.setLoginId(member.getLoginId().toLowerCase());
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m->{
                    throw new MemberException("このIDは既に使用されています。");
                });
    }

    public Member login(String loginId, String password) {
        String lowerLoginId = loginId.toLowerCase();
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

    @Transactional
    public void updateMember(Long memberId, Member updateParam) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("該当する会員情報が見つかりません。"));

        findMember.setEmail(updateParam.getEmail());
        findMember.setName(updateParam.getName());
        findMember.setAddress(updateParam.getAddress());

        String password = updateParam.getPassword();
        if (password != null && !password.isEmpty()) {

            String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";

            if (!Pattern.matches(passwordPattern, password)) {
                throw new MemberException("パスワードは8〜20文字で、英字、数字、特殊文字（@$!%*#?&）をすべて含める必要があります。");
            }

            findMember.setPassword(password);
        }
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("該当する会員が見つかりません。"));
    }
}
