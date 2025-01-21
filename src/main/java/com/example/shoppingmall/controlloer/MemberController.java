package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.repository.MemberRepository;
import com.example.shoppingmall.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signUp(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(Member member) {
        memberService.save(member);
        return "redirect:/login";
    }

}

