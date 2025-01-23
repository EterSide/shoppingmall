package com.example.shoppingmall.controlloer;

import com.example.shoppingmall.entitiy.Member;
import com.example.shoppingmall.entitiy.status.Role;
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
    public String signUp() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(Member member, HttpSession session) {
        System.out.println("asd");
        memberService.save(member);
        session.setAttribute("member", member);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        session.removeAttribute("member");
        return "redirect:/";
    }

    @GetMapping("/login")
    public String logIn() {
        return "login";
    }

    @PostMapping("/login")
    public String logIn(@RequestParam String userId, @RequestParam String password, HttpSession session,Model model) {
        Optional<Member> member = memberService.idCheck(userId);

        if (member.isPresent()) {
            Member mem = member.get();
            if (mem.getPassword().equals(password)) {
                session.setAttribute("member", mem);
                return "redirect:/";
            } else {
                model.addAttribute("error", "패스워드가 틀립니다.");
            } 
        } else {
            model.addAttribute("error", "아이디가 틀립니다.");
        }

        return "login";
    }

}

