package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.FriendList;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping(value = "")
    public String get(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        FriendList friendList = friendService.findFriends(loginUser.getAccountId());
        model.addAttribute("following", friendList.getFollowing());
        model.addAttribute("followers", friendList.getFollowers());
        model.addAttribute("requesting", friendList.getRequesting());
        model.addAttribute("requested", friendList.getRequested());
        return "friend";
    }

    @GetMapping(value = "")
    public String allow(@RequestParam Integer accountId, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.allow(accountId, loginUser.getAccountId());
        return "redirect:/friend";
    }

    @GetMapping(value = "")
    public String remove(@RequestParam Integer accountId, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.remove(accountId, loginUser.getAccountId());
        return "redirect:/friend";
    }

}
