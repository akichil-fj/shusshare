package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.RecruitmentService;
import net.akichil.shusshare.service.exception.NoAccessResourceException;
import net.akichil.shusshare.validation.AddGroup;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    private final MessageSourceHelper messageSourceHelper;

    public RecruitmentController(RecruitmentService recruitmentService, MessageSourceHelper messageSourceHelper) {
        this.recruitmentService = recruitmentService;
        this.messageSourceHelper = messageSourceHelper;
    }

    @GetMapping(path = "/list")
    public String list(Model model,
                       @AuthenticationPrincipal LoginUser loginUser,
                       @RequestParam(value = "createdById", required = false) Integer createdById,
                       @RequestParam(value = "shusshaId", required = false) Integer shusshaId) {
        RecruitmentSelector selector = RecruitmentSelector.builder()
                .accountId(loginUser.getAccountId())
                .createdById(createdById)
                .shusshaId(shusshaId)
                .startDate(LocalDate.now())
                .build();
        List<RecruitmentDetail> recruitments = recruitmentService.find(selector);
        model.addAttribute("recruitments", recruitments);
        return "recruitment/list";
    }

    @GetMapping(path = "/add")
    public String getAdd(@ModelAttribute(name = "recruitment") RecruitmentForEdit recruitment,
                         @RequestParam("shusshaId") Integer shusshaId,
                         Model model) {
        model.addAttribute("genreList", getRecruitmentGenre());
        recruitment.setShusshaId(shusshaId);
        return "recruitment/add";
    }

    @PostMapping(path = "/add")
    public String add(@Validated(AddGroup.class) @ModelAttribute(name = "recruitment") RecruitmentForEdit recruitment,
                      BindingResult bindingResult,
                      Model model,
                      RedirectAttributes attributes,
                      @AuthenticationPrincipal LoginUser loginUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genreList", getRecruitmentGenre());
            return "recruitment/add";
        }
        if (recruitment.getDeadlineStr() != null) {
            // 時間をパース
            try {
                // 一旦現在時刻をもとに締切を設定。Serviceで出社日時に合わせる。
                String targetStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd ")) + recruitment.getDeadlineStr();
                recruitment.setDeadline(LocalDateTime.parse(targetStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
            } catch (DateTimeParseException exception) {
                model.addAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.add.error.dateformat"));
                model.addAttribute("genreList", getRecruitmentGenre());
                return "recruitment/add";
            }
        }
        if (recruitment.getCapacityStr() != null) {
            recruitment.setCapacity(Integer.parseInt(recruitment.getCapacityStr()));
        }
        // 作成者を登録
        recruitment.setCreatedBy(loginUser.getAccountId());
        // 状態を登録
        recruitment.setStatus(RecruitmentStatus.OPENED);

        try {
            recruitmentService.add(recruitment);
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.add.error"));
            model.addAttribute("genreList", getRecruitmentGenre());
            return "recruitment/add";
        } catch (NoAccessResourceException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.add.error.wrongshussha"));
            return "redirect:/recruitment/list";
        }

        return "redirect:/recruitment/list";
    }

    @GetMapping(path = "/detail/{recruitmentId}")
    public String detail(Model model,
                         RedirectAttributes attributes,
                         @AuthenticationPrincipal LoginUser loginUser,
                         @PathVariable("recruitmentId") Integer recruitmentId) {
        try {
            model.addAttribute("recruitment", recruitmentService.get(recruitmentId, loginUser.getAccountId()));
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.detail.error.notfound"));
            return "redirect:/error";
        }
        model.addAttribute("accountId", loginUser.getAccountId());

        return "recruitment/detail";
    }

    private List<RecruitmentGenre> getRecruitmentGenre() {
        return List.of(RecruitmentGenre.LUNCH, RecruitmentGenre.CAFE);
    }

}
