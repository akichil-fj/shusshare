package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.service.ShusshaService;
import net.akichil.shusshare.service.exception.DataNotUpdatedException;
import net.akichil.shusshare.service.exception.IllegalDateRegisterException;
import net.akichil.shusshare.service.exception.PasswordNotMatchException;
import net.akichil.shusshare.test.TestWithUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
public class MyPageControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private ShusshaService shusshaService;

    @Autowired
    private MockMvc mockMvc;

    private final static String URL_PREFIX = "http://localhost:8080/mypage";

    @Test
    @TestWithUser
    public void testGetMyPage() throws Exception {
        Integer accountId = 1;
        Account account = new Account();
        account.setAccountId(accountId);
        account.setUserId("user");
        account.setUserName("user_name");
        account.setStatus(AccountStatus.NORMAL);
        account.setShusshaCount(3);

        ShusshaList shusshaList = new ShusshaList();
        List<Shussha> pastShussha = new ArrayList<>();
        List<Shussha> futureShussha = new ArrayList<>();
        shusshaList.setFutureShussha(pastShussha);
        shusshaList.setPastShussha(futureShussha);

        Mockito.doReturn(account).when(accountService).get(accountId);
        Mockito.doReturn(shusshaList).when(shusshaService).list(accountId);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("mypage/mypage"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attribute("pastShussha", pastShussha))
                .andExpect(model().attribute("futureShussha", futureShussha));

        Mockito.verify(accountService, Mockito.times(1)).get(accountId);
    }

    @Test
    @TestWithUser
    public void testAddShusshaSuccess() throws Exception {
        ArgumentMatcher<Shussha> matcher = argument -> {
            assertEquals(1, argument.getAccountId());
            assertEquals(LocalDate.of(2022, 6, 18), argument.getDate());
            assertEquals(ShusshaStatus.TOBE, argument.getStatus()); // ステータスが一致するか
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .param("date", "2022/6/18")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("msg", "成功：出社登録"));

        Mockito.verify(shusshaService, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    @TestWithUser
    public void testAddFailWrongFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .param("date", "aaaaaaaaaaa")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("errorMsg", "失敗：日付のフォーマットを確認してください。"));

        Mockito.verify(shusshaService, Mockito.times(0)).add(any(Shussha.class));
    }

    @Test
    @TestWithUser
    public void testAddFailDuplicated() throws Exception {
        Mockito.doThrow(DataNotUpdatedException.class).when(shusshaService).add(any(Shussha.class));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .param("date", "2022/12/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("errorMsg", "失敗：その日の出社は登録済みです。"));

        Mockito.verify(shusshaService, Mockito.times(1)).add(any(Shussha.class));
    }

    @Test
    @TestWithUser
    public void testAddFailPastDateTobe() throws Exception {
        Mockito.doThrow(IllegalDateRegisterException.class).when(shusshaService).add(any(Shussha.class));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .param("date", "2022/12/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("errorMsg", "失敗：明日以降の日付を入力してください。"));

        Mockito.verify(shusshaService, Mockito.times(1)).add(any(Shussha.class));
    }

    @Test
    @TestWithUser
    public void testRemoveSuccess() throws Exception {
        final Integer accountId = 1;
        final LocalDate date = LocalDate.of(2022, 6,18);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/remove")
                        .param("date", "2022/6/18")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("msg", "成功：出社登録削除"));

        Mockito.verify(shusshaService, Mockito.times(1)).remove(accountId, date);
    }

    @Test
    @TestWithUser
    public void testRemoveFailWrongFormat() throws Exception {
        final Integer accountId = 1;

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/remove")
                        .param("date", "aaaaaaaaa")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("errorMsg", "失敗：日付のフォーマットを確認してください。"));

        Mockito.verify(shusshaService, Mockito.times(0))
                .remove(ArgumentMatchers.eq(accountId), ArgumentMatchers.any(LocalDate.class));
    }

    @Test
    @TestWithUser
    public void testRemoveFailResourceNotFound() throws Exception {
        final Integer accountId = 1;
        final LocalDate date = LocalDate.of(2022, 6,18);

        Mockito.doThrow(ResourceNotFoundException.class).when(shusshaService).remove(accountId, date);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/remove")
                        .param("date", "2022/6/18")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("errorMsg", "失敗：削除対象が見つかりませんでした。"));

        Mockito.verify(shusshaService, Mockito.times(1)).remove(accountId, date);
    }

    @Test
    @TestWithUser
    public void testGetEditPage() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        Integer accountId = 1;
        account.setIsPrivate(false);
        Mockito.doReturn(account).when(accountService).get(accountId);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/edit")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("mypage/edit"))
                .andExpect(model().attribute("account", account));

        Mockito.verify(accountService, Mockito.times(1)).get(accountId);
    }

    @Test
    @TestWithUser
    public void testUpdate() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        account.setAccountId(1);
        account.setUserId("user_id");
        account.setUserName("user_name");
        account.setStatus(AccountStatus.PRIVATE);
        account.setIsPrivate(true);

        ArgumentMatcher<AccountForUserEdit> matcher = argument -> {
            assertEquals(account.getAccountId(), argument.getAccountId());
            assertEquals(account.getUserId(), argument.getUserId());
            assertEquals(account.getUserName(), argument.getUserName());
            assertEquals(account.getStatus(), argument.getStatus());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit")
                        .param("userId", account.getUserId())
                        .param("userName", account.getUserName())
                        .param("isPrivate", account.getIsPrivate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("msg", "成功：ユーザ情報更新"));

        Mockito.verify(accountService, Mockito.times(1)).set(Mockito.argThat(matcher));
    }

    @Test
    @TestWithUser
    public void testUpdateFailByInvalidInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit")
                        .param("userId", "@$a%#??????????????!!") // invalid
                        .param("userName", "user")
                        .param("isPrivate", "false")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/edit"))
                .andExpect(model().hasErrors());

        Mockito.verify(accountService, Mockito.times(0)).set(any(Account.class));
    }

    @Test
    @TestWithUser
    public void testUpdateFailByDuplicatedUserId() throws Exception {
        Mockito.doThrow(DataIntegrityViolationException.class).when(accountService).set(any(Account.class));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit")
                        .param("userId", "user_id")
                        .param("userName", "user")
                        .param("isPrivate", "false")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/mypage/edit"))
                .andExpect(flash().attribute("errorMsg", "すでに登録ずみのユーザIDです。"));

        Mockito.verify(accountService, Mockito.times(1)).set(any(Account.class));
    }

    @Test
    @TestWithUser
    public void testUpdateFailByResourceNotFoundException() throws Exception {
        Mockito.doThrow(ResourceNotFoundException.class).when(accountService).set(any(Account.class));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit")
                        .param("userId", "user_id")
                        .param("userName", "user")
                        .param("isPrivate", "false")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/mypage/edit"))
                .andExpect(flash().attribute("errorMsg", "何らかの理由により更新に失敗しました。"));

        Mockito.verify(accountService, Mockito.times(1)).set(any(Account.class));
    }

    @Test
    @TestWithUser
    public void testGetUpdatePassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/edit-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("mypage/edit-password"));
    }

    @Test
    @TestWithUser
    public void testUpdatePassword() throws Exception {
        final Integer accountId = 1;
        final String oldPassword = "old_Password";
        final String newPassword = "new_password";
        final String confirmPassword = "new_password";

        ArgumentMatcher<EditPassword> matcher = argument -> {
            assertEquals(oldPassword, argument.getOldPassword());
            assertEquals(newPassword, argument.getNewPassword());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit-password")
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("msg", "成功：パスワード更新"));

        Mockito.verify(accountService, Mockito.times(1)).setPassword(Mockito.eq(accountId), Mockito.argThat(matcher));
    }

    @Test
    @TestWithUser
    public void testUpdatePasswordFailByInvalidPassword() throws Exception {
        final Integer accountId = 1;
        final String oldPassword = "old_Password";
        final String newPassword = "new_password";
        final String confirmPassword = "bad_password";

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit-password")
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/edit-password"))
                .andExpect(model().hasErrors());

        Mockito.verify(accountService, Mockito.times(0)).setPassword(Mockito.eq(accountId), Mockito.any());
    }

    @Test
    @TestWithUser
    public void testUpdatePasswordFailNotMatchPassword() throws Exception {
        final Integer accountId = 1;
        final String oldPassword = "old_Password";
        final String newPassword = "new_password";
        final String confirmPassword = "new_password";

        ArgumentMatcher<EditPassword> matcher = argument -> {
            assertEquals(oldPassword, argument.getOldPassword());
            assertEquals(newPassword, argument.getNewPassword());
            return true;
        };

        Mockito.doThrow(PasswordNotMatchException.class).when(accountService).setPassword(Mockito.eq(accountId), Mockito.argThat(matcher));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit-password")
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/mypage/edit-password"))
                .andExpect(flash().attribute("errorMsg", "失敗：旧パスワードが一致しません"));

        Mockito.verify(accountService, Mockito.times(1)).setPassword(Mockito.eq(accountId), Mockito.argThat(matcher));
    }

    @Test
    @TestWithUser
    public void testUpdatePasswordFailResourceNotFound() throws Exception {
        final Integer accountId = 1;
        final String oldPassword = "old_Password";
        final String newPassword = "new_password";
        final String confirmPassword = "new_password";

        ArgumentMatcher<EditPassword> matcher = argument -> {
            assertEquals(oldPassword, argument.getOldPassword());
            assertEquals(newPassword, argument.getNewPassword());
            return true;
        };

        Mockito.doThrow(ResourceNotFoundException.class).when(accountService).setPassword(Mockito.eq(accountId), Mockito.argThat(matcher));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/edit-password")
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .param("confirmPassword", confirmPassword)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/mypage/edit-password"))
                .andExpect(flash().attribute("errorMsg", "何らかの理由により更新に失敗しました。"));

        Mockito.verify(accountService, Mockito.times(1)).setPassword(Mockito.eq(accountId), Mockito.argThat(matcher));
    }

}
