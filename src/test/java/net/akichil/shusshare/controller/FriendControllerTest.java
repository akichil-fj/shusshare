package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.FriendList;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.service.FriendService;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
public class FriendControllerTest {

    @MockBean
    private FriendService friendService;

    @Autowired
    private MockMvc mockMvc;

    private final static String URL_PREFIX = "http://localhost:8080/friend";

    @Test
    @TestWithUser
    public void testGet() throws Exception {
        Integer accountId = 1;
        FriendList friendList = new FriendList();
        ArrayList<FriendDetail> following = new ArrayList<>();
        ArrayList<FriendDetail> followers = new ArrayList<>();
        ArrayList<FriendDetail> requesting = new ArrayList<>();
        ArrayList<FriendDetail> requested = new ArrayList<>();
        friendList.setFollowing(following);
        friendList.setFollowers(followers);
        friendList.setRequesting(requesting);
        friendList.setRequested(requested);

        Mockito.doReturn(friendList).when(friendService).findFriends(accountId);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("friend"))
                .andExpect(model().attribute("following", following))
                .andExpect(model().attribute("followers", followers))
                .andExpect(model().attribute("requesting", requesting))
                .andExpect(model().attribute("requested", requested));

        Mockito.verify(friendService, Mockito.times(1)).findFriends(accountId);
    }

    @Test
    @TestWithUser
    public void testFind() throws Exception {
        String keyword = "keyword";
        ArrayList<FriendDetail> users = new ArrayList<>();
        ArgumentMatcher<UserSelector> matcher = argument -> {
            assertEquals(1, argument.getAccountIdFrom());
            assertEquals(keyword, argument.getUserName());
            return true;
        };

        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/find")
                        .param("userName", keyword)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("friend/find"))
                .andExpect(model().attribute("keyword", keyword))
                .andExpect(model().attribute("users", users));

        Mockito.verify(friendService, Mockito.times(1)).findAllUser(Mockito.argThat(matcher));
    }

    @Test
    @TestWithUser
    public void testAllow() throws Exception {
        Integer friendAccountId = 3;

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/allow")
                        .param("accountId", friendAccountId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:/friend"));

        Mockito.verify(friendService, Mockito.times(1)).allow(friendAccountId, 1);
    }

    @Test
    @TestWithUser
    public void testAdd() throws Exception {
        Integer friendAccountId = 3;
        String redirectPath = "/friend/パス";
        String encodedPath = "/friend/%E3%83%91%E3%82%B9"; // "/friend/パス"

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/add")
                        .param("accountId", friendAccountId.toString())
                        .param("redirectPath", redirectPath)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:" + encodedPath));

        Mockito.verify(friendService, Mockito.times(1)).request(friendAccountId, 1);
    }

    @Test
    @TestWithUser
    public void testAddFailByWrongURI() throws Exception {
        Integer friendAccountId = 3;
        String redirectPath = "/friend/:?##?&&";

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/add")
                        .param("accountId", friendAccountId.toString())
                        .param("redirectPath", redirectPath)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(flash().attribute("errorMsg", "リダイレクトURLでエラーが発生しました。"))
                .andExpect(view().name("redirect:/error"));

        Mockito.verify(friendService, Mockito.times(0)).request(friendAccountId, 1);
    }

    @Test
    @TestWithUser
    public void testRemove() throws Exception {
        Integer friendAccountId = 3;
        String redirectPath = "/friend/パス";
        String encodedPath = "/friend/%E3%83%91%E3%82%B9"; // "/friend/パス"

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/remove")
                        .param("accountId", friendAccountId.toString())
                        .param("redirectPath", redirectPath)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(view().name("redirect:" + encodedPath));

        Mockito.verify(friendService, Mockito.times(1)).remove(friendAccountId, 1);
    }

    @Test
    @TestWithUser
    public void testRemoveFailByWrongUri() throws Exception {
        Integer friendAccountId = 3;
        String redirectPath = "/friend/??&&##";

        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + "/remove")
                        .param("accountId", friendAccountId.toString())
                        .param("redirectPath", redirectPath)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(flash().attribute("errorMsg", "リダイレクトURLでエラーが発生しました。"))
                .andExpect(view().name("redirect:/error"));

        Mockito.verify(friendService, Mockito.times(0)).remove(friendAccountId, 1);
    }


}
