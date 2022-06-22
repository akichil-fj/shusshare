package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.FriendList;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    private final MessageSourceHelper messageSourceHelper;

    public FriendController(FriendService friendService, MessageSourceHelper messageSourceHelper) {
        this.friendService = friendService;
        this.messageSourceHelper = messageSourceHelper;
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
    public String allow(@RequestParam Integer accountId, @RequestParam String redirectPath,
                        @AuthenticationPrincipal LoginUser loginUser, RedirectAttributes attributes) {
        String encodedPath;
        try {
            encodedPath = new URI(redirectPath).toASCIIString();
        } catch (URISyntaxException e) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("friend.allow.error.redirect"));
            return "redirect:/error";
        }
        friendService.allow(accountId, loginUser.getAccountId());
        return "redirect:" + encodedPath;
    }

    @GetMapping(path = "/find")
    public String find(Model model, UserSelector selector, @AuthenticationPrincipal LoginUser loginUser) {
        selector.setAccountIdFrom(loginUser.getAccountId()); // 閲覧者を入れる
        model.addAttribute("keyword", Optional.ofNullable(selector.getUserName()).orElse(""));
        model.addAttribute("users", friendService.findAllUser(selector));
        return "friend/find";
    }

    @PostMapping(path = "/add")
    public String add(@RequestParam Integer accountId, @RequestParam String redirectPath,
                      @AuthenticationPrincipal LoginUser loginUser, RedirectAttributes attributes) {
        String encodedPath;
        try {
            encodedPath = new URI(redirectPath).toASCIIString();
        } catch (URISyntaxException e) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("friend.add.error.redirect"));
            return "redirect:/error";
        }
        friendService.request(accountId, loginUser.getAccountId());
        return "redirect:" + encodedPath;
    }

    @PostMapping(value = "/remove")
    public String remove(@RequestParam Integer accountId, @RequestParam String redirectPath,
                         @AuthenticationPrincipal LoginUser loginUser, RedirectAttributes attributes) {
        String encodedPath;
        try {
            encodedPath = new URI(redirectPath).toASCIIString();
        } catch (URISyntaxException e) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("friend.remove.error.redirect"));
            return "redirect:/error";
        }
        friendService.remove(accountId, loginUser.getAccountId());
        return "redirect:" + encodedPath;
    }

}
