package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.service.AccountService;
import net.akichil.shusshare.service.FriendService;
import net.akichil.shusshare.service.ShusshaService;
import net.akichil.shusshare.test.TestWithUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
public class HomeControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private FriendService friendService;

    @MockBean
    private ShusshaService shusshaService;

    @Autowired
    private MockMvc mockMvc;

    private final static String URL_PREFIX = "http://localhost:8080/home";

    @Test
    @TestWithUser
    public void testGet() throws Exception {
        final Integer accountId = 1;
        ArrayList<FriendDetail> friends = new ArrayList<>();
        Account account = new Account();
        account.setUserId("user");
        Shussha shussha = new Shussha();

        Mockito.doReturn(friends).when(friendService).findGoOfficeFriend(accountId);
        Mockito.doReturn(account).when(accountService).get(accountId);
        Mockito.doReturn(shussha).when(shusshaService).get(accountId, LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("goOfficeFriend", friends))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attribute("shussha", shussha));

        Mockito.verify(friendService, Mockito.times(1)).findGoOfficeFriend(accountId);
        Mockito.verify(accountService, Mockito.times(1)).get(accountId);
        Mockito.verify(shusshaService, Mockito.times(1)).get(accountId, LocalDate.now());
    }

    @Test
    @TestWithUser
    public void testGetWithoutShussha() throws Exception {
        final Integer accountId = 1;
        ArrayList<FriendDetail> friends = new ArrayList<>();
        Account account = new Account();
        account.setUserId("user");

        Mockito.doReturn(friends).when(friendService).findGoOfficeFriend(accountId);
        Mockito.doReturn(account).when(accountService).get(accountId);
        Mockito.doThrow(ResourceNotFoundException.class).when(shusshaService).get(accountId, LocalDate.now());

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("goOfficeFriend", friends))
                .andExpect(model().attribute("account", account));

        Mockito.verify(friendService, Mockito.times(1)).findGoOfficeFriend(accountId);
        Mockito.verify(accountService, Mockito.times(1)).get(accountId);
        Mockito.verify(shusshaService, Mockito.times(1)).get(accountId, LocalDate.now());
    }

    @Test
    @TestWithUser(accountId = 2)
    public void testAddShussha() throws Exception {
        ArgumentMatcher<Shussha> matcher = argument -> {
            assertEquals(2, argument.getAccountId());
            assertEquals(LocalDate.now(), argument.getDate()); // 日付が一致するか
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/create")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/home"));

        Mockito.verify(shusshaService, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    @TestWithUser(accountId = 2)
    public void testRemoveShussha() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/shussha/remove")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/home"));

        Mockito.verify(shusshaService, Mockito.times(1)).remove(2, LocalDate.now());
    }


}
