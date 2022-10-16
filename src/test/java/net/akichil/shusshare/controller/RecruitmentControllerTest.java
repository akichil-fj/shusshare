package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.service.RecruitmentService;
import net.akichil.shusshare.service.exception.NoAccessResourceException;
import net.akichil.shusshare.service.exception.ParticipantsOverCapacityException;
import net.akichil.shusshare.test.TestWithUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
@TestWithUser
public class RecruitmentControllerTest {

    @MockBean
    private RecruitmentService recruitmentService;

    @Autowired
    private MockMvc mockMvc;

    private final static String URL_PREFIX = "http://localhost:8080/recruitment";

    @Test
    public void testList() throws Exception {
        RecruitmentSelector selector = RecruitmentSelector.builder()
                .accountId(1).startDate(LocalDate.now()).build();
        List<RecruitmentDetail> recruitments = new ArrayList<>();
        ArgumentMatcher<RecruitmentSelector> matcher = argument -> {
            assertEquals(selector.getAccountId(), argument.getAccountId());
            assertEquals(selector.getStartDate(), argument.getStartDate());
            return true;
        };
        Mockito.doReturn(recruitments).when(recruitmentService).find(Mockito.argThat(matcher));

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/list")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/list"))
                .andExpect(model().attribute("recruitments", recruitments));

        Mockito.verify(recruitmentService, Mockito.times(1)).find(Mockito.argThat(matcher));
    }

    @Test
    public void testMine() throws Exception {
        RecruitmentSelector selector = RecruitmentSelector.builder()
                .accountId(1).createdById(1).build();
        List<RecruitmentDetail> recruitments = new ArrayList<>();
        ArgumentMatcher<RecruitmentSelector> matcher = argument -> {
            assertEquals(selector.getAccountId(), argument.getAccountId());
            assertEquals(selector.getCreatedById(), argument.getCreatedById());
            return true;
        };
        Mockito.doReturn(recruitments).when(recruitmentService).find(Mockito.argThat(matcher));

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/mine")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/mine"))
                .andExpect(model().attribute("recruitments", recruitments));

        Mockito.verify(recruitmentService, Mockito.times(1)).find(Mockito.argThat(matcher));
    }

    @Test
    public void testGetAdd() throws Exception {
        Integer shusshaId = 1;
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setShusshaId(shusshaId);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/add")
                        .param("shusshaId", shusshaId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/add"))
                .andExpect(model().attribute("recruitment", recruitment));
    }

    @Test
    public void testAddSuccess() throws Exception {
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setCreatedBy(1);
        recruitment.setShusshaId(2);
        recruitment.setGenre(RecruitmentGenre.LUNCH);
        recruitment.setTitle("test title");
        recruitment.setDeadlineStr("13:25");
        recruitment.setCapacityStr("4");

        ArgumentMatcher<RecruitmentForEdit> matcher = argument -> {
            assertEquals(recruitment.getCreatedBy(), argument.getCreatedBy());
            assertEquals(recruitment.getShusshaId(), argument.getShusshaId());
            assertEquals(recruitment.getTitle(), argument.getTitle());
            assertEquals(recruitment.getGenre(), argument.getGenre());
            assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 25)), argument.getDeadline());
            assertEquals(4, argument.getCapacity());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/add")
                        .param("shusshaId", String.valueOf(recruitment.getShusshaId()))
                        .param("title", recruitment.getTitle())
                        .param("genre", recruitment.getGenre().name())
                        .param("deadlineStr", recruitment.getDeadlineStr())
                        .param("capacityStr", recruitment.getCapacityStr())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/list"));

        Mockito.verify(recruitmentService, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    public void testAddFailByValidationError() throws Exception {
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setCreatedBy(1);
        recruitment.setShusshaId(2);
        recruitment.setGenre(RecruitmentGenre.LUNCH);
        recruitment.setTitle("");
        recruitment.setDeadlineStr("date");
        recruitment.setCapacityStr("abc");

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/add")
                        .param("shusshaId", String.valueOf(recruitment.getShusshaId()))
                        .param("title", recruitment.getTitle())
                        .param("genre", recruitment.getGenre().name())
                        .param("deadlineStr", recruitment.getDeadlineStr())
                        .param("capacityStr", recruitment.getCapacityStr())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/add"))
                .andExpect(model().attributeHasFieldErrors("recruitment", "title", "deadlineStr", "capacityStr"));

        Mockito.verify(recruitmentService, Mockito.times(0)).add(any());
    }

    @Test
    public void testAddFailByDateParseError() throws Exception {
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setCreatedBy(1);
        recruitment.setShusshaId(2);
        recruitment.setGenre(RecruitmentGenre.LUNCH);
        recruitment.setTitle("test title");
        recruitment.setDeadlineStr("99:99");

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/add")
                        .param("shusshaId", String.valueOf(recruitment.getShusshaId()))
                        .param("title", recruitment.getTitle())
                        .param("genre", recruitment.getGenre().name())
                        .param("deadlineStr", recruitment.getDeadlineStr())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/add"))
                .andExpect(model().attribute("errorMsg", "失敗：日付のフォーマットを確認してください。"));

        Mockito.verify(recruitmentService, Mockito.times(0)).add(any());
    }

    @Test
    public void testAddFailByNoAccessError() throws Exception {
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setCreatedBy(1);
        recruitment.setShusshaId(2);
        recruitment.setGenre(RecruitmentGenre.LUNCH);
        recruitment.setTitle("test title");
        Mockito.doThrow(NoAccessResourceException.class).when(recruitmentService).add(any());

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/add")
                        .param("shusshaId", String.valueOf(recruitment.getShusshaId()))
                        .param("title", recruitment.getTitle())
                        .param("genre", recruitment.getGenre().name())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/list"))
                .andExpect(flash().attribute("errorMsg", "失敗：出社の指定にエラーがありました。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).add(any());
    }

    @Test
    public void testGetDetail() throws Exception {
        Integer recruitmentId = 4;
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        FriendDetail friendDetail = new FriendDetail();
        friendDetail.setAccountId(10);
        friendDetail.setUserId("test_user");
        friendDetail.setUserName("hoge");
        friendDetail.setStatus(FriendStatus.FOLLOWED);
        friendDetail.setAccountStatus(AccountStatus.NORMAL);

        recruitmentDetail.setCreatedFriend(friendDetail);
        recruitmentDetail.setRecruitmentId(4);
        recruitmentDetail.setTitle("test");
        recruitmentDetail.setCreatedBy(3);
        recruitmentDetail.setGenre(RecruitmentGenre.LUNCH);
        recruitmentDetail.setStatus(RecruitmentStatus.OPENED);
        Mockito.doReturn(recruitmentDetail).when(recruitmentService).get(recruitmentId, 1);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/detail/4")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/detail"))
                .andExpect(model().attribute("recruitment", recruitmentDetail));

        Mockito.verify(recruitmentService, Mockito.times(1)).get(recruitmentId, 1);
    }

    @Test
    public void testGetDetailFailByNotFound() throws Exception {
        Integer recruitmentId = 4;
        Mockito.doThrow(ResourceNotFoundException.class).when(recruitmentService).get(recruitmentId, 1);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/detail/4")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/error"))
                .andExpect(flash().attribute("errorMsg", "お探しの募集は見つかりませんでした。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).get(recruitmentId, 1);
    }

    @Test
    public void testEditGet() throws Exception {
        Integer recruitmentId = 3;
        RecruitmentForEdit recruitmentForEdit = new RecruitmentForEdit();
        recruitmentForEdit.setRecruitmentId(3);
        recruitmentForEdit.setTitle("test");
        recruitmentForEdit.setGenre(RecruitmentGenre.CAFE);
        recruitmentForEdit.setDeadline(LocalDateTime.of(2022, 1, 1, 15, 34));
        recruitmentForEdit.setDeadlineStr("15:34");
        RecruitmentDetail recruitment = new RecruitmentDetail();
        recruitment.setRecruitmentId(3);
        recruitment.setTitle("test");
        recruitment.setGenre(RecruitmentGenre.CAFE);
        recruitment.setDeadline(LocalDateTime.of(2022, 1, 1, 15, 34));
        Mockito.doReturn(recruitment).when(recruitmentService).get(recruitmentId, 1);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/edit")
                        .param("recruitmentId", recruitmentId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/edit"))
                .andExpect(model().attribute("recruitment", recruitmentForEdit));
    }

    @Test
    public void testEditSuccess() throws Exception {
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setRecruitmentId(2);
        recruitment.setCreatedBy(1);
        recruitment.setGenre(RecruitmentGenre.CAFE);
        recruitment.setTitle("test title");
        recruitment.setDeadlineStr("13:25");
        recruitment.setCapacityStr("4");

        ArgumentMatcher<RecruitmentForEdit> matcher = argument -> {
            assertEquals(recruitment.getRecruitmentId(), argument.getRecruitmentId());
            assertEquals(recruitment.getCreatedBy(), argument.getCreatedBy());
            assertEquals(recruitment.getTitle(), argument.getTitle());
            assertEquals(recruitment.getGenre(), argument.getGenre());
            assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 25)), argument.getDeadline());
            assertEquals(4, argument.getCapacity());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit")
                        .param("recruitmentId", String.valueOf(recruitment.getRecruitmentId()))
                        .param("title", recruitment.getTitle())
                        .param("genre", recruitment.getGenre().name())
                        .param("deadlineStr", recruitment.getDeadlineStr())
                        .param("capacityStr", recruitment.getCapacityStr())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/2"))
                .andExpect(flash().attribute("msg", "成功：募集内容更新"));

        Mockito.verify(recruitmentService, Mockito.times(1)).set(Mockito.argThat(matcher));
    }

    @Test
    public void testEditFailByValidationError() throws Exception {
        RecruitmentForEdit recruitment = new RecruitmentForEdit();
        recruitment.setRecruitmentId(2);
        recruitment.setCreatedBy(1);
        recruitment.setGenre(RecruitmentGenre.CAFE);
        recruitment.setTitle("test_title test_title test_title test_title test_title");
        recruitment.setDeadlineStr("100:100");
        recruitment.setCapacityStr("abc");

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit")
                        .param("recruitmentId", String.valueOf(recruitment.getRecruitmentId()))
                        .param("title", recruitment.getTitle())
                        .param("genre", recruitment.getGenre().name())
                        .param("deadlineStr", recruitment.getDeadlineStr())
                        .param("capacityStr", recruitment.getCapacityStr())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruitment/edit"))
                .andExpect(model().attributeHasFieldErrors("recruitment", "title", "deadlineStr", "capacityStr"));

        Mockito.verify(recruitmentService, Mockito.times(0)).add(any());
    }

    @Test
    public void testParticipateSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/participate")
                        .param("recruitmentId", "2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/2"))
                .andExpect(flash().attribute("msg", "成功：参加完了"));
        Mockito.verify(recruitmentService, Mockito.times(1)).addParticipants(any(), any());
    }

    @Test
    public void testParticipateFailByNoAccess() throws Exception {
        Mockito.doThrow(NoAccessResourceException.class).when(recruitmentService).addParticipants(eq(2), eq(List.of(1)));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/participate")
                        .param("recruitmentId", "2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/2"))
                .andExpect(flash().attribute("errorMsg", "この募集に参加するには作成者をフォローする必要があります。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).addParticipants(any(), any());
    }

    @Test
    public void testParticipateFailOverCapacity() throws Exception {
        Mockito.doThrow(ParticipantsOverCapacityException.class).when(recruitmentService).addParticipants(eq(2), eq(List.of(1)));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/participate")
                        .param("recruitmentId", "2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/2"))
                .andExpect(flash().attribute("errorMsg", "この募集は満員のため参加できません。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).addParticipants(any(), any());
    }

    @Test
    public void testParticipateFailByDuplicate() throws Exception {
        Mockito.doThrow(DataIntegrityViolationException.class).when(recruitmentService).addParticipants(eq(2), eq(List.of(1)));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/participate")
                        .param("recruitmentId", "2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/2"))
                .andExpect(flash().attribute("errorMsg", "この募集は参加済みです。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).addParticipants(any(), any());
    }

    @Test
    public void testLeave() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/leave")
                        .param("recruitmentId", "3")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/3"))
                .andExpect(flash().attribute("msg", "成功：退出完了"));

        Mockito.verify(recruitmentService, Mockito.times(1)).removeParticipants(any(), any());
    }

    @Test
    public void testLeaveFailByNotFound() throws Exception {
        Mockito.doThrow(ResourceNotFoundException.class).when(recruitmentService).removeParticipants(eq(3), eq(List.of(1)));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/leave")
                        .param("recruitmentId", "3")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/3"))
                .andExpect(flash().attribute("errorMsg", "失敗：退出対象の募集が見つかりませんでした。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).removeParticipants(any(), any());
    }

    @Test
    public void testCancel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/cancel/4")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/4"))
                .andExpect(flash().attribute("msg", "キャンセル済み"));

        Mockito.verify(recruitmentService, Mockito.times(1)).cancel(4, 1);
    }

    @Test
    public void testCancelFailByNoAccess() throws Exception {
        Mockito.doThrow(NoAccessResourceException.class).when(recruitmentService).cancel(4, 1);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/cancel/4")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/4"))
                .andExpect(flash().attribute("errorMsg", "削除権限がありません。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).cancel(4, 1);
    }

    @Test
    public void testReopen() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/reopen/5")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/5"))
                .andExpect(flash().attribute("msg", "再募集済み"));

        Mockito.verify(recruitmentService, Mockito.times(1)).reopen(5, 1);
    }

    @Test
    public void testReopenFailByNoAccess() throws Exception {
        Mockito.doThrow(NoAccessResourceException.class).when(recruitmentService).reopen(5, 1);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/reopen/5")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/recruitment/detail/5"))
                .andExpect(flash().attribute("errorMsg", "再募集の権限がありません。"));

        Mockito.verify(recruitmentService, Mockito.times(1)).reopen(5, 1);
    }

}
