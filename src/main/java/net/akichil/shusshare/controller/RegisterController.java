package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.AccountForUserEdit;
import net.akichil.shusshare.entity.AccountStatus;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.validation.AddGroup;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final AccountService accountService;

    private final MessageSourceHelper messageSourceHelper;

    private final PasswordEncoder passwordEncoder;

    public RegisterController(AccountService accountService, MessageSourceHelper messageSourceHelper, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.messageSourceHelper = messageSourceHelper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(path = "")
    public String get(@ModelAttribute(name = "account") AccountForUserEdit account) {
        return "register";
    }

    @PostMapping(path = "")
    public String add(@Validated(AddGroup.class) @ModelAttribute(name = "account") AccountForUserEdit account,
                      BindingResult bindingResult,
                      Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // パスワードをハッシュ化
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (account.getIsPrivate()) {
            account.setStatus(AccountStatus.PRIVATE);
        } else {
            account.setStatus(AccountStatus.NORMAL);
        }
        try {
            accountService.add(account);
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("errorMsg", messageSourceHelper.getMessage("account.register.duplicate"));
            return "register";
        }

        return "redirect:/register/success";
    }

    @GetMapping(path = "/success")
    public String addSuccess() {
        return "register/success";
    }

}
