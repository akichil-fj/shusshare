package net.akichil.shusshare.controller;

import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.service.FriendService;
import net.akichil.shusshare.service.ShusshaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FriendService friendService;

    private final AccountService accountService;

    private final ShusshaService shusshaService;

    public HomeController(FriendService friendService, AccountService accountService, ShusshaService shusshaService) {
        this.friendService = friendService;
        this.accountService = accountService;
        this.shusshaService = shusshaService;
    }

    @GetMapping(value = "")
    public String get(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("goOfficeFriend", friendService.findGoOfficeFriend(loginUser.getAccountId()));
        model.addAttribute("account", accountService.get(loginUser.getAccountId()));
        try {
            model.addAttribute("shussha", shusshaService.get(loginUser.getAccountId(), LocalDate.now()));
        } catch (ResourceNotFoundException exception) {
            model.addAttribute("shussha", null);
        }
        return "home";
    }

}
