package net.akichil.shusshare.controller;

import net.akichil.shusshare.security.LoginUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping(path = "")
    public String get(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser != null) {
            return "redirect:/home";
        }
        return "login";
    }

}
