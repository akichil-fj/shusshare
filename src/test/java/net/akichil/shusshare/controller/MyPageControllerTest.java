package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.service.ShusshaService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        Mockito.doReturn(account).when(accountService).get(accountId);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("mypage"))
                .andExpect(model().attribute("account", account));

        Mockito.verify(accountService, Mockito.times(1)).get(accountId);
    }

    @Test
    @TestWithUser
    public void testAddShusshaSuccess() throws Exception {
        ArgumentMatcher<Shussha> matcher = argument -> {
            assertEquals(1, argument.getAccountId());
            assertEquals(LocalDate.of(2022, 6,18), argument.getDate());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .param("date", "2022-6-18")
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

        Mockito.verify(shusshaService, Mockito.times(0)).add(ArgumentMatchers.any(Shussha.class));
    }

    @Test
    @TestWithUser
    public void testAddFailDuplicated() throws Exception {
        Mockito.doThrow(DataIntegrityViolationException.class).when(shusshaService).add(ArgumentMatchers.any(Shussha.class));

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .param("date", "2022-12-1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/mypage"))
                .andExpect(flash().attribute("errorMsg", "失敗：その日の出社は登録済みです。"));

        Mockito.verify(shusshaService, Mockito.times(1)).add(ArgumentMatchers.any(Shussha.class));
    }



}
