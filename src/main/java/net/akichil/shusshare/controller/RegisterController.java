package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.AccountForUserEdit;
import net.akichil.shusshare.entity.AccountStatus;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.validation.AddGroup;
import org.springframework.stereotype.Controller;
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

    public RegisterController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "")
    public String get(@ModelAttribute(name = "account") AccountForUserEdit account) {
        return "register";
    }

    @PostMapping(path = "")
    public String add(@Validated(AddGroup.class) @ModelAttribute(name = "account") AccountForUserEdit account,
                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (account.getIsPrivate()) {
            account.setStatus(AccountStatus.PRIVATE);
        } else {
            account.setStatus(AccountStatus.NORMAL);
        }
        accountService.add(account);
        return "redirect:/register/success";
    }

    @GetMapping(path = "/success")
    public String addSuccess() {
        return "register/success";
    }

}
