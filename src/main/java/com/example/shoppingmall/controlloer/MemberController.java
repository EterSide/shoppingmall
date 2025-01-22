package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.repository.MemberRepository;
import com.example.shoppingmall.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("content", "signup");
        return "layout";
    }

    @PostMapping("/signup")
    public String signUp(Member member) {
        System.out.println("asd");
        memberService.save(member);
        return "index";
    }

    @GetMapping("/login")
    public String logIn(Model model) {
        model.addAttribute("content", "login");
        return "layout";
    }

    @GetMapping("/login2")
    public String logIn2(Model model) {
        return "login2";
    }

    @PostMapping("/login2")
    public String logIn2(@RequestParam String userId, @RequestParam String password, HttpSession session,Model model) {
        Optional<Member> member = memberService.idCheck(userId);

        if (member.isPresent()) {
            if (member.get().getPassword().equals(password)) {
                session.setAttribute("member", member.get());
                return "index";
            } else {
                model.addAttribute("error", "패스워드가 틀립니다.");
            }
        } else {
            model.addAttribute("error", "아이디가 틀립니다.");
        }
        return "login2";
    }

    @PostMapping("/login")
    public String logIn(@RequestParam String userId, @RequestParam String password, HttpSession session,Model model) {
        Optional<Member> member = memberService.idCheck(userId);

        if (member.isPresent()) {
            if (member.get().getPassword().equals(password)) {
                session.setAttribute("member", member.get());
                return "index";
            } else {
                model.addAttribute("error", "패스워드가 틀립니다.");
            } 
        } else {
            model.addAttribute("error", "아이디가 틀립니다.");
        }
        model.addAttribute("content", "login");
        return "layout";
    }

}

