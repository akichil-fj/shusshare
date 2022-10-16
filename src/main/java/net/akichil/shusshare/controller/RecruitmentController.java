package net.akichil.shusshare.controller;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.helper.MessageSourceHelper;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.security.LoginUser;
import net.akichil.shusshare.service.RecruitmentService;
import net.akichil.shusshare.service.exception.NoAccessResourceException;
import net.akichil.shusshare.service.exception.ParticipantsOverCapacityException;
import net.akichil.shusshare.validation.AddGroup;
import net.akichil.shusshare.validation.SetGroup;
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

    @GetMapping(path = "/mine")
    public String mine(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        RecruitmentSelector selector = RecruitmentSelector.builder()
                .accountId(loginUser.getAccountId())
                .createdById(loginUser.getAccountId())
                .build();
        List<RecruitmentDetail> recruitments = recruitmentService.find(selector);
        model.addAttribute("recruitments", recruitments);
        return "recruitment/mine";
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
        } catch (NoAccessResourceException | ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.add.error.wrongshussha"));
            return "redirect:/recruitment/list";
        }

        return "redirect:/recruitment/list";
    }

    @GetMapping(path = "/edit")
    public String getEdit(@ModelAttribute(name = "recruitment") RecruitmentForEdit recruitment,
                          @RequestParam("recruitmentId") Integer recruitmentId,
                          Model model,
                          RedirectAttributes attributes,
                          @AuthenticationPrincipal LoginUser loginUser) {
        try {
            Recruitment recruitmentDetail = recruitmentService.get(recruitmentId, loginUser.getAccountId());
            recruitment.set(recruitmentDetail);
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.detail.error.notfound"));
            return "redirect:/error";
        }
        model.addAttribute("genreList", getRecruitmentGenre());
        return "recruitment/edit";
    }

    @PostMapping(path = "/edit")
    public String edit(@Validated(SetGroup.class) @ModelAttribute(name = "recruitment") RecruitmentForEdit recruitment,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes attributes,
                       @AuthenticationPrincipal LoginUser loginUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genreList", getRecruitmentGenre());
            return "recruitment/edit";
        }
        if (recruitment.getDeadlineStr() != null) {
            // 時間をパース
            try {
                // 一旦現在時刻をもとに締切を設定。Serviceで出社日時に合わせる。
                String targetStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd ")) + recruitment.getDeadlineStr();
                recruitment.setDeadline(LocalDateTime.parse(targetStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
            } catch (DateTimeParseException exception) {
                model.addAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.edit.error.dateformat"));
                model.addAttribute("genreList", getRecruitmentGenre());
                return "recruitment/edit";
            }
        } else {
            recruitment.setDeadline(null);
        }
        if (recruitment.getCapacityStr() != null) {
            recruitment.setCapacity(Integer.parseInt(recruitment.getCapacityStr()));
        } else {
            recruitment.setCapacityStr(null);
        }
        // 作成者を登録
        recruitment.setCreatedBy(loginUser.getAccountId());

        try {
            recruitmentService.set(recruitment);
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.edit.error"));
            model.addAttribute("genreList", getRecruitmentGenre());
            return "recruitment/edit";
        } catch (NoAccessResourceException | ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.edit.error.wrongshussha"));
            return "redirect:/recruitment/detail/" + recruitment.getRecruitmentId();
        }

        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("recruitment.edit.success"));
        return "redirect:/recruitment/detail/" + recruitment.getRecruitmentId();
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

    @PostMapping(path = "/cancel/{recruitmentId}")
    public String cancel(RedirectAttributes attributes,
                         @AuthenticationPrincipal LoginUser loginUser,
                         @PathVariable("recruitmentId") Integer recruitmentId) {
        try {
            recruitmentService.cancel(recruitmentId, loginUser.getAccountId());
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.cancel.error.notfound"));
            return "redirect:/error";
        } catch (NoAccessResourceException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.cancel.error.noaccess"));
            return "redirect:/recruitment/detail/" + recruitmentId;
        }

        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("recruitment.cancel.success"));
        return "redirect:/recruitment/detail/" + recruitmentId;
    }

    @PostMapping(path = "/reopen/{recruitmentId}")
    public String reopen(RedirectAttributes attributes,
                         @AuthenticationPrincipal LoginUser loginUser,
                         @PathVariable("recruitmentId") Integer recruitmentId) {
        try {
            recruitmentService.reopen(recruitmentId, loginUser.getAccountId());
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.reopen.error.notfound"));
            return "redirect:/error";
        } catch (NoAccessResourceException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.reopen.error.noaccess"));
            return "redirect:/recruitment/detail/" + recruitmentId;
        }

        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("recruitment.reopen.success"));
        return "redirect:/recruitment/detail/" + recruitmentId;
    }

    @PostMapping(path = "/participate")
    public String participate(RedirectAttributes attributes,
                              @AuthenticationPrincipal LoginUser loginUser,
                              @RequestParam(value = "recruitmentId") Integer recruitmentId) {
        try {
            recruitmentService.addParticipants(recruitmentId, List.of(loginUser.getAccountId()));
        } catch (NoAccessResourceException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.participate.error.noaccess"));
            return "redirect:/recruitment/detail/" + recruitmentId;
        } catch (ParticipantsOverCapacityException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.participate.error.capacityover"));
            return "redirect:/recruitment/detail/" + recruitmentId;
        } catch (DataIntegrityViolationException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.participate.error.duplicate"));
            return "redirect:/recruitment/detail/" + recruitmentId;
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("recruitment.participate.success"));
        return "redirect:/recruitment/detail/" + recruitmentId;
    }

    @PostMapping(path = "/leave")
    public String leave(RedirectAttributes attributes,
                        @AuthenticationPrincipal LoginUser loginUser,
                        @RequestParam(value = "recruitmentId") Integer recruitmentId) {
        try {
            recruitmentService.removeParticipants(recruitmentId, List.of(loginUser.getAccountId()));
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute("errorMsg", messageSourceHelper.getMessage("recruitment.leave.error.notfound"));
            return "redirect:/recruitment/detail/" + recruitmentId;
        }
        attributes.addFlashAttribute("msg", messageSourceHelper.getMessage("recruitment.leave.success"));
        return "redirect:/recruitment/detail/" + recruitmentId;
    }

    private List<RecruitmentGenre> getRecruitmentGenre() {
        return List.of(RecruitmentGenre.LUNCH, RecruitmentGenre.CAFE);
    }

}
