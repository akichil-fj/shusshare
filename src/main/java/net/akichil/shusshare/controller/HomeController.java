package net.akichil.shusshare.controller;

import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FriendService friendService;

    public HomeController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping(value = "")
    public String get(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("goOfficeFriend", friendService.findGoOfficeFriend(loginUser.getAccountId()));
        return "home";
    }

}
