package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.FriendList;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @PostMapping(value = "/allow")
    public String allow(@RequestParam Integer accountId, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.allow(accountId, loginUser.getAccountId());
        return "redirect:/friend";
    }

    @PostMapping(path = "/add")
    public String add(@RequestParam Integer accountId, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.request(accountId, loginUser.getAccountId());
        return "redirect:/friend";
    }

    @PostMapping(value = "/remove")
    public String remove(@RequestParam Integer accountId, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.remove(accountId, loginUser.getAccountId());
        return "redirect:/friend";
    }

    @GetMapping(path = "/find")
    public String find(Model model, UserSelector selector, @AuthenticationPrincipal LoginUser loginUser) {
        selector.setAccountIdFrom(loginUser.getAccountId()); // 閲覧者を入れる
        model.addAttribute("keyword", selector.getUserName());
        model.addAttribute("users", friendService.findAllUser(selector));
        return "friend/find";
    }

    @PostMapping(path = "/find/add")
    public String addFromFind(@RequestParam Integer accountId, @RequestParam String keyword, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.request(accountId, loginUser.getAccountId());
        return "redirect:/friend/find?userName=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
    }

    @PostMapping(path = "/find/remove")
    public String removeFromFind(@RequestParam Integer accountId, @RequestParam String keyword, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.remove(accountId, loginUser.getAccountId());
        return "redirect:/friend/find?userName=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
    }

    @PostMapping(path = "/user/add")
    public String add(@RequestParam Integer accountId, @RequestParam String redirectPath, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.request(accountId, loginUser.getAccountId());
        return "redirect:" + redirectPath;
    }

    @PostMapping(value = "/user/remove")
    public String remove(@RequestParam Integer accountId, @RequestParam String redirectPath, @AuthenticationPrincipal LoginUser loginUser) {
        friendService.remove(accountId, loginUser.getAccountId());
        return "redirect:" + redirectPath;
    }

}
