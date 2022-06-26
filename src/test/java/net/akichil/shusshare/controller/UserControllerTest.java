package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.AccountStatus;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.FriendStatus;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.service.FriendService;
import net.akichil.shusshare.test.TestWithUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private FriendService friendService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @TestWithUser
    public void getUserPage() throws Exception {
        String userId = "user_id";
        Integer accountIdFrom = 1;
        Integer userAccountId = 5;
        FriendDetail user = new FriendDetail();
        user.setAccountId(userAccountId);
        user.setUserId(userId);
        user.setStatus(FriendStatus.NONE);
        user.setAccountStatus(AccountStatus.PRIVATE);
        Mockito.doReturn(user).when(friendService).findFriendByUserId(userId, accountIdFrom);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("isMyself", false))
                .andExpect(view().name("user/index"));

        Mockito.verify(friendService, Mockito.times(1)).findFriendByUserId(userId, accountIdFrom);
    }

    @Test
    @TestWithUser
    public void getMyUserPage() throws Exception {
        String userId = "user_id";
        Integer accountIdFrom = 1;
        Integer userAccountId = 1;
        FriendDetail user = new FriendDetail();
        user.setAccountId(userAccountId);
        user.setUserId(userId);
        user.setStatus(FriendStatus.NONE);
        user.setAccountStatus(AccountStatus.NORMAL);
        Mockito.doReturn(user).when(friendService).findFriendByUserId(userId, accountIdFrom);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("isMyself", true))
                .andExpect(view().name("user/index"));

        Mockito.verify(friendService, Mockito.times(1)).findFriendByUserId(userId, accountIdFrom);
    }

    @Test
    @TestWithUser
    public void getUserPageFailUserNotFound() throws Exception {
        String userId = "not_exist_user";
        Integer accountIdFrom = 1;
        Mockito.doThrow(ResourceNotFoundException.class).when(friendService).findFriendByUserId(userId, accountIdFrom);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMsg", "ユーザが見つかりません"))
                .andExpect(view().name("user/index"));

        Mockito.verify(friendService, Mockito.times(1)).findFriendByUserId(userId, accountIdFrom);
    }

}