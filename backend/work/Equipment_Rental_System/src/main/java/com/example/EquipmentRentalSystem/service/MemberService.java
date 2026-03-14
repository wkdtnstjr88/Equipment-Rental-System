package com.example.EquipmentRentalSystem.service;

import com.example.EquipmentRentalSystem.entity.Member;
import com.example.EquipmentRentalSystem.exception.MemberException;
import com.example.EquipmentRentalSystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
