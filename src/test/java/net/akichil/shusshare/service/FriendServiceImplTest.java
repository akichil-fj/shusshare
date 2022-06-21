package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.repository.FriendRepository;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FriendServiceImplTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private FriendServiceImpl target;

    private AutoCloseable closeable;

    @BeforeEach
    public void beforeEach() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void afterEach() throws Exception {
        closeable.close();
    }

    @Test
    public void testFindAllUser() {
        UserSelector selector = new UserSelector();
        List<FriendDetail> friendDetails = List.of(new FriendDetail(), new FriendDetail());

        Mockito.doReturn(friendDetails).when(friendRepository).findAllUser(selector);

        List<FriendDetail> results = target.findAllUser(selector);

        assertEquals(2, results.size());
        Mockito.verify(friendRepository, Mockito.times(1)).findAllUser(selector);
    }

    @Test
    public void testFindFriends() {
        final Integer accountId = 10;

        FriendDetail friend1 = new FriendDetail();
        friend1.setAccountId(1);
        friend1.setStatus(FriendStatus.FOLLOWED);
        FriendDetail friend2 = new FriendDetail();
        friend2.setAccountId(2);
        friend2.setStatus(FriendStatus.REQUESTED);
        FriendDetail friend3 = new FriendDetail();
        friend3.setAccountId(3);
        friend3.setStatus(FriendStatus.FOLLOWED);
        FriendDetail friend4 = new FriendDetail();
        friend4.setAccountId(4);
        friend4.setStatus(FriendStatus.REQUESTED);

        List<FriendDetail> fromUser = List.of(friend1, friend2);
        List<FriendDetail> toUser = List.of(friend3, friend4);

        Mockito.doReturn(fromUser).when(friendRepository).findFriendFromUser(accountId);
        Mockito.doReturn(toUser).when(friendRepository).findFriendsToUser(accountId);

        FriendList results = target.findFriends(accountId);

        assertEquals(1, results.getFollowing().size());
        assertEquals(FriendStatus.FOLLOWED, results.getFollowing().get(0).getStatus());
        assertEquals(1, results.getRequesting().size());
        assertEquals(FriendStatus.REQUESTED, results.getRequesting().get(0).getStatus());
        assertEquals(1, results.getFollowers().size());
        assertEquals(FriendStatus.FOLLOWED, results.getFollowers().get(0).getStatus());
        assertEquals(1, results.getRequested().size());
        assertEquals(FriendStatus.REQUESTED, results.getRequested().get(0).getStatus());

        Mockito.verify(friendRepository, Mockito.times(1)).findFriendFromUser(accountId);
        Mockito.verify(friendRepository, Mockito.times(1)).findFriendsToUser(accountId);
    }

    @Test
    public void testFindGoOfficeFriend() {
        final Integer accountId = 1;
        final LocalDate nowDate = LocalDate.now();
        List<FriendDetail> friends = List.of(new FriendDetail(), new FriendDetail());

        Mockito.doReturn(friends).when(friendRepository).findGoOfficeFriend(accountId, nowDate, null);

        List<FriendDetail> results = target.findGoOfficeFriend(accountId);

        assertEquals(2, results.size());
        Mockito.verify(friendRepository, Mockito.times(1)).findGoOfficeFriend(accountId, nowDate, null);
    }

    @Test
    public void testFindFriendByAccountId() {
        final Integer accountId = 1;
        final Integer accountIdFrom = 3;
        final FriendDetail friendDetail = new FriendDetail();

        Mockito.doReturn(friendDetail).when(friendRepository).findFriendByAccountId(accountId, accountIdFrom);

        FriendDetail result = target.findFriendByAccountId(accountId, accountIdFrom);

        assertNotNull(result);
        Mockito.verify(friendRepository, Mockito.times(1)).findFriendByAccountId(accountId, accountIdFrom);
    }

    @Test
    public void testFindFriendByUserId() {
        final String userId = "user_id";
        final Integer accountIdFrom = 3;
        final FriendDetail friendDetail = new FriendDetail();

        Mockito.doReturn(friendDetail).when(friendRepository).findFriendByAccountId(userId, accountIdFrom);

        FriendDetail result = target.findFriendByUserId(userId, accountIdFrom);

        assertNotNull(result);
        Mockito.verify(friendRepository, Mockito.times(1)).findFriendByAccountId(userId, accountIdFrom);
    }

    @Test
    public void testAdd() {
        final Friend friend = new Friend();

        target.add(friend);

        Mockito.verify(friendRepository, Mockito.times(1)).add(friend);
    }

    @Test
    public void testUpdate() {
        final Friend friend = new Friend();

        target.set(friend);

        Mockito.verify(friendRepository, Mockito.times(1)).set(friend);
    }

    @Test
    public void testRemoveSuccess() {
        final Integer accountId = 1;
        final Integer accountIdFrom = 3;
        final FriendDetail friend = new FriendDetail();

        Mockito.doReturn(friend).when(friendRepository).findFriendByAccountId(accountId, accountIdFrom);

        target.remove(accountId, accountIdFrom);

        Mockito.verify(friendRepository, Mockito.times(1)).findFriendByAccountId(accountId, accountIdFrom);
        Mockito.verify(friendRepository, Mockito.times(1)).remove(friend);
    }

    @Test
    public void testRemoveFailResourceNotFound() {
        final Integer accountId = 1;
        final Integer accountIdFrom = 3;
        final FriendDetail friend = new FriendDetail();

        Mockito.doThrow(ResourceNotFoundException.class).when(friendRepository).findFriendByAccountId(accountId, accountIdFrom);

        assertThrows(ResourceNotFoundException.class, () -> target.remove(accountId, accountIdFrom));

        Mockito.verify(friendRepository, Mockito.times(1)).findFriendByAccountId(accountId, accountIdFrom);
        Mockito.verify(friendRepository, Mockito.times(0)).remove(friend);
    }

    @Test
    public void testRequestToNormalAccount() {
        final Integer accountId = 1;
        final Integer accountIdFrom = 3;
        Account account = new Account();
        account.setStatus(AccountStatus.NORMAL);
        ArgumentMatcher<Friend> matcher = argument -> {
            assertEquals(accountId, argument.getAccountIdTo());
            assertEquals(accountIdFrom, argument.getAccountIdFrom());
            assertEquals(FriendStatus.FOLLOWED, argument.getStatus());
            return true;
        };

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        target.request(accountId, accountIdFrom);

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
        Mockito.verify(friendRepository, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    public void testRequestToPrivateAccount() {
        final Integer accountId = 1;
        final Integer accountIdFrom = 3;
        Account account = new Account();
        account.setStatus(AccountStatus.PRIVATE);
        ArgumentMatcher<Friend> matcher = argument -> {
            assertEquals(accountId, argument.getAccountIdTo());
            assertEquals(accountIdFrom, argument.getAccountIdFrom());
            assertEquals(FriendStatus.REQUESTED, argument.getStatus());
            return true;
        };

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        target.request(accountId, accountIdFrom);

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
        Mockito.verify(friendRepository, Mockito.times(1)).add(Mockito.argThat(matcher));
    }

    @Test
    public void testAllow() {
        final Integer accountId = 1;
        final Integer accountIdFrom = 3;
        ArgumentMatcher<Friend> matcher = argument -> {
            assertEquals(accountId, argument.getAccountIdTo());
            assertEquals(accountIdFrom, argument.getAccountIdFrom());
            assertEquals(FriendStatus.FOLLOWED, argument.getStatus());
            return true;
        };

        target.allow(accountId, accountIdFrom);

        Mockito.verify(friendRepository, Mockito.times(1)).set(Mockito.argThat(matcher));
    }

}
