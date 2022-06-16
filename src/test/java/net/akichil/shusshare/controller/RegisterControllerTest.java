package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountForUserEdit;
import net.akichil.shusshare.entity.AccountStatus;
import net.akichil.shusshare.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
public class RegisterControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final static String URL_PREFIX = "http://localhost:8080/register";

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testAddSuccessWithNormalUser() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        account.setUserId("user_1");
        account.setUserName("ユーザ");
        account.setPassword("password");
        account.setConfirmPassword("password");
        account.setIsPrivate(false);
        account.setStatus(AccountStatus.NORMAL);

        ArgumentMatcher<Account> matcher = argument -> {
            assertEquals(account.getUserId(), argument.getUserId());
            assertEquals(account.getUserName(), argument.getUserName());
            assertTrue(passwordEncoder.matches(account.getPassword(), argument.getPassword())); //パスワードがエンコードされているか
            assertEquals(account.getStatus(), argument.getStatus());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX)
                        .param("userId", account.getUserId())
                        .param("userName", account.getUserName())
                        .param("password", account.getPassword())
                        .param("confirmPassword", account.getConfirmPassword())
                        .param("isPrivate", account.getIsPrivate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/register/success"));

        Mockito.verify(accountService, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    public void testAddSuccessWithPrivateUser() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        account.setUserId("user_1");
        account.setUserName("ユーザ");
        account.setPassword("password");
        account.setConfirmPassword("password");
        account.setIsPrivate(true);
        account.setStatus(AccountStatus.PRIVATE);

        ArgumentMatcher<Account> matcher = argument -> {
            assertEquals(account.getUserId(), argument.getUserId());
            assertEquals(account.getUserName(), argument.getUserName());
            assertTrue(passwordEncoder.matches(account.getPassword(), argument.getPassword())); //パスワードがエンコードされているか
            assertEquals(account.getStatus(), argument.getStatus());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX)
                        .param("userId", account.getUserId())
                        .param("userName", account.getUserName())
                        .param("password", account.getPassword())
                        .param("confirmPassword", account.getConfirmPassword())
                        .param("isPrivate", account.getIsPrivate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/register/success"));

        Mockito.verify(accountService, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    public void testAddFailByDuplicatedUserId() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        account.setUserId("user_1");
        account.setUserName("ユーザ");
        account.setPassword("password");
        account.setConfirmPassword("password");
        account.setIsPrivate(false);
        account.setStatus(AccountStatus.NORMAL);

        Mockito.doThrow(DataIntegrityViolationException.class).when(accountService).add(ArgumentMatchers.any(Account.class));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX)
                        .param("userId", account.getUserId())
                        .param("userName", account.getUserName())
                        .param("password", account.getPassword())
                        .param("confirmPassword", account.getConfirmPassword())
                        .param("isPrivate", account.getIsPrivate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(model().attribute("errorMsg", "すでに登録ずみのユーザIDです。"))
                .andExpect(view().name("register"));

        Mockito.verify(accountService, Mockito.times(1)).add(ArgumentMatchers.any(Account.class));
    }

    @Test
    public void testAddFailByNotMatchConfirmPassword() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        account.setUserId("user_1");
        account.setUserName("ユーザ");
        account.setPassword("password");
        account.setConfirmPassword("badPassword");
        account.setIsPrivate(false);
        account.setStatus(AccountStatus.NORMAL);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX)
                        .param("userId", account.getUserId())
                        .param("userName", account.getUserName())
                        .param("password", account.getPassword())
                        .param("confirmPassword", account.getConfirmPassword())
                        .param("isPrivate", account.getIsPrivate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("register"));

        Mockito.verify(accountService, Mockito.times(0)).add(ArgumentMatchers.any(Account.class));
    }

    @Test
    public void testAddFailByWrongUserId() throws Exception {
        AccountForUserEdit account = new AccountForUserEdit();
        account.setUserId("user_1_@user_1^@user_1xxxxxxxxxxxxx");
        account.setUserName("ユーザ");
        account.setPassword("password");
        account.setConfirmPassword("password");
        account.setIsPrivate(false);
        account.setStatus(AccountStatus.NORMAL);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX)
                        .param("userId", account.getUserId())
                        .param("userName", account.getUserName())
                        .param("password", account.getPassword())
                        .param("confirmPassword", account.getConfirmPassword())
                        .param("isPrivate", account.getIsPrivate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("register"));

        Mockito.verify(accountService, Mockito.times(0)).add(ArgumentMatchers.any(Account.class));
    }

    @Test
    public void testSuccessGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/success")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("register/success"));
    }

}
