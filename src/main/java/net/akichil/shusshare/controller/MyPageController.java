package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.service.ShusshaService;
import net.akichil.shusshare.service.exception.DataNotUpdatedException;
import net.akichil.shusshare.service.exception.IllegalDateRegisterException;
import net.akichil.shusshare.service.exception.PasswordNotMatchException;
import net.akichil.shusshare.validation.SetGroup;
import net.akichil.shusshare.validation.SetPasswordGroup;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
        ShusshaList shusshaList = shusshaService.list(loginUser.getAccountId());
        model.addAttribute("pastShussha", shusshaList.getPastShussha());
        model.addAttribute("futureShussha", shusshaList.getFutureShussha());
        return "mypage/mypage";
    }

    @PostMapping(path = "/shussha/create")
    public String addShussha(@AuthenticationPrincipal LoginUser loginUser,
                             @RequestParam("date") String date,
                             RedirectAttributes attributes) {
        Shussha shussha = new Shussha();
        shussha.setAccountId(loginUser.getAccountId());
        shussha.setStatus(ShusshaStatus.TOBE); // 出社予定
        try {
            shussha.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d")));
        } catch (DateTimeParseException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.register.error.format"));
            return "redirect:/mypage";
        }
        try {
            shusshaService.add(shussha);
        } catch (DataNotUpdatedException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.register.error.duplicate"));
            return "redirect:/mypage";
        } catch (IllegalDateRegisterException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.register.error.tobedate"));
            return "redirect:/mypage";
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("shussha.register.success"));
        return "redirect:/mypage";
    }

    @PostMapping(path = "/shussha/remove")
    public String removeShussa(@AuthenticationPrincipal LoginUser loginUser,
                               @RequestParam("date") String dateString,
                               RedirectAttributes attributes) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-M-d"));
        } catch (DateTimeParseException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.remove.error.format"));
            return "redirect:/mypage";
        }
        try {
            shusshaService.remove(loginUser.getAccountId(), date);
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("shussha.remove.error.notfound"));
            return "redirect:/mypage";
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("shussha.remove.success"));
        return "redirect:/mypage";
    }

    @GetMapping(path = "/edit")
    public String getEditPage(@AuthenticationPrincipal LoginUser loginUser, @ModelAttribute(name = "account") AccountForUserEdit accountForUserEdit) {
        Account account = accountService.get(loginUser.getAccountId());
        accountForUserEdit.set(account);
        return "mypage/edit";
    }

    @PostMapping(path = "/edit")
    public String edit(@AuthenticationPrincipal LoginUser loginUser,
                       @ModelAttribute(name = "account") @Validated(SetGroup.class) AccountForUserEdit accountForUserEdit,
                       BindingResult bindingResult,
                       RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "mypage/edit";
        }

        accountForUserEdit.setAccountId(loginUser.getAccountId());
        accountForUserEdit.setStatus(accountForUserEdit.getIsPrivate() ? AccountStatus.PRIVATE : AccountStatus.NORMAL);

        try {
            accountService.set(accountForUserEdit);
        } catch (DataIntegrityViolationException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("account.register.duplicate"));
            return "redirect:/mypage/edit";
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("account.edit.error"));
            return "redirect:/mypage/edit";
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("account.edit.success"));
        return "redirect:/mypage";
    }

    @GetMapping(path = "/edit-password")
    public String getEditPasswordPage(@ModelAttribute(name = "password") EditPassword editPassword) {
        return "mypage/edit-password";
    }

    @PostMapping(path = "/edit-password")
    public String editPassword(@AuthenticationPrincipal LoginUser loginUser,
                       @ModelAttribute(name = "password") @Validated(SetPasswordGroup.class) EditPassword editPassword,
                       BindingResult bindingResult,
                       RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "mypage/edit-password";
        }

        try {
            accountService.setPassword(loginUser.getAccountId(), editPassword);
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("account.edit.error"));
            return "redirect:/mypage/edit-password";
        } catch (PasswordNotMatchException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("account.edit.password.error.notmatch"));
            return "redirect:/mypage/edit-password";
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("account.edit.password.success"));
        return "redirect:/mypage";
    }

}
