package net.akichil.shusshare.security;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountSelector;
import net.akichil.shusshare.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDetailsServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private UserDetailsServiceImpl target;

    private AutoCloseable closeable;

    @BeforeEach
    public void beforeEach() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void afterEach() throws Exception {
        closeable.close();
    }

    /**
     * ユーザが1件見つかった時
     */
    @Test
    public void testLoadSuccess() {
        final Integer accountId = 1;
        final String userId = "user";
        final String password = "$2a$10$Z0TppA8dZ9wPjH1LIsTn1OVj6J.XIJq1GcA9ck4jYFMBoLE65xmoO"; // password
        Account account = new Account();
        account.setAccountId(accountId);
        account.setUserId(userId);
        account.setPassword(password);
        ArgumentMatcher<AccountSelector> matcher = argument -> {
            assertEquals(userId, argument.getUserId());
            return true;
        };
        Mockito.doReturn(List.of(account)).when(accountRepository).findList(Mockito.argThat(matcher));

        LoginUser result = target.loadUserByUsername(userId);

        assertEquals(accountId, result.getAccountId());
        assertEquals(userId, result.getUsername());
        assertEquals(password, result.getPassword());
        assertIterableEquals(AccountRole.USER.getGradAuthority(), result.getAuthorities());
        Mockito.verify(accountRepository, Mockito.times(1)).findList(Mockito.argThat(matcher));
    }

    /**
     * ユーザが見つからなかった時
     */
    @Test
    public void testLoadFailUserNotFound() {
        final String userId = "user";
        ArgumentMatcher<AccountSelector> matcher = argument -> {
            assertEquals(userId, argument.getUserId());
            return true;
        };
        Mockito.doReturn(new ArrayList<>()).when(accountRepository).findList(Mockito.argThat(matcher));

        assertThrows(UsernameNotFoundException.class, () -> target.loadUserByUsername(userId));

        Mockito.verify(accountRepository, Mockito.times(1)).findList(Mockito.argThat(matcher));
    }

}
