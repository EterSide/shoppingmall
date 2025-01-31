package com.example.shoppingmall.service;

import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(Member member) {
        Member mem = new Member();
        mem.setUserId(member.getUserId());
        mem.setPassword(member.getPassword());
        mem.setName(member.getName());
        mem.setEmail(member.getEmail());
        mem.setPhone(member.getPhone());
        mem.setAddress(member.getAddress());
        Member save = memberRepository.save(mem);
        return save;
    }

    public Optional<Member> passwordCheck(String id, String password) {
        return memberRepository.findByUserIdAndPassword(id, password);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> idCheck(String id) {
        return memberRepository.findByUserId(id);
    }

}
