package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.service.ShusshaService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private final AccountService accountService;

    private final ShusshaService shusshaService;

    private final MessageSourceHelper messageSourceHelper;

    public MyPageController(AccountService accountService, ShusshaService shusshaService, MessageSourceHelper messageSourceHelper) {
        this.accountService = accountService;
        this.shusshaService = shusshaService;
        this.messageSourceHelper = messageSourceHelper;
    }

    @GetMapping(path = "")
    public String get(@AuthenticationPrincipal LoginUser loginUser, Model model,
                      @ModelAttribute("date") String date) {
        model.addAttribute("account", accountService.get(loginUser.getAccountId()));
        return "mypage";
    }

    @PostMapping(path = "/shussha/create")
    public String addShussha(@AuthenticationPrincipal LoginUser loginUser,
                             @RequestParam("date") String date,
                             RedirectAttributes attributes) {
        Shussha shussha = new Shussha();
        shussha.setAccountId(loginUser.getAccountId());
        try {
            shussha.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d")));
        } catch (DateTimeParseException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.register.error.format"));
            return "redirect:/mypage";
        }
        try {
            shusshaService.add(shussha);
        } catch (DataIntegrityViolationException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.register.error.duplicate"));
            return "redirect:/mypage";
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("shussha.register.success"));
        return "redirect:/mypage";
    }

}
