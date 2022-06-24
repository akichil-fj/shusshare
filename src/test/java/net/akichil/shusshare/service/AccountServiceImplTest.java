package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl target;

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
    public void testGet() {
        final Integer accountId = 1;
        Account account = new Account();
        account.setAccountId(accountId);

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        Account result = target.get(accountId);

        assertEquals(account, result);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
    }

    @Test
    public void testGetByUserId() {
        final String userId = "user";
        Account account = new Account();
        account.setUserId(userId);

        Mockito.doReturn(account).when(accountRepository).findOne(userId);

        Account result = target.get(userId);

        assertEquals(account, result);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(userId);
    }


    @Test
    public void testAdd() {
        Account account = new Account();

        target.add(account);

        Mockito.verify(accountRepository, Mockito.times(1)).add(account);
    }

    @Test
    public void testSet() {
        Account account = new Account();

        target.set(account);

        Mockito.verify(accountRepository, Mockito.times(1)).set(account);
    }

    @Test
    public void testRemove() {
        final Integer accountId = 1;
        Account account = new Account();
        account.setAccountId(accountId);

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        target.remove(accountId);

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
        Mockito.verify(accountRepository, Mockito.times(1)).remove(account);
    }

    @Test
    public void testRemoveWhenResourceNotFound() {
        final Integer accountId = 1;

        Mockito.doThrow(ResourceNotFoundException.class).when(accountRepository).findOne(accountId);

        assertThrows(ResourceNotFoundException.class, () -> target.remove(accountId));

        Mockito.verify(accountRepository, Mockito.times(0)).remove(any(Account.class));
    }

}