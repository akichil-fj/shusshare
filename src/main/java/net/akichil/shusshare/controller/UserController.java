package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private final FriendService friendService;

    private final MessageSourceHelper messageSourceHelper;

    public UserController(FriendService friendService, MessageSourceHelper messageSourceHelper) {
        this.friendService = friendService;
        this.messageSourceHelper = messageSourceHelper;
    }


    @GetMapping(path = "/{userId}")
    public String getUser(@PathVariable("userId") String userId,
                          @AuthenticationPrincipal LoginUser loginUser,
                          Model model) {
        FriendDetail friend;
        try {
            friend = friendService.findFriendByUserId(userId, loginUser.getAccountId());
        } catch (ResourceNotFoundException exception) {
            model.addAttribute("errorMsg", messageSourceHelper.getMessage("user.notfound"));
            return "user/index";
        }

        model.addAttribute("isMyself", friend.getAccountId().equals(loginUser.getAccountId()));
        model.addAttribute("user", friend);
        return "user/index";
    }

}
