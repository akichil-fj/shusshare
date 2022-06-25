package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.entity.ShusshaStatus;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.repository.ShusshaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShusshaServiceImplTest {

    @Mock
    private ShusshaRepository shusshaRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ShusshaServiceImpl target;

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
        final LocalDate date = LocalDate.now();
        Shussha shussha = new Shussha();

        Mockito.doReturn(shussha).when(shusshaRepository).find(accountId, date);

        final Shussha result = target.get(accountId, date);

        assertEquals(shussha, result);
        Mockito.verify(shusshaRepository, Mockito.times(1)).find(accountId, date);
    }

    @Test
    public void testAddNew() {
        // setup
        final Integer accountId = 1;
        final LocalDate date = LocalDate.now();
        final ShusshaStatus status = ShusshaStatus.DONE;
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        shussha.setDate(date);
        shussha.setStatus(status);

        final int shusshaCount = 3;
        Account account = new Account();
        account.setShusshaCount(shusshaCount);

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        // when
        target.add(shussha);

        // then
        assertEquals(shusshaCount + 1, account.getShusshaCount());
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
        Mockito.verify(accountRepository, Mockito.times(1)).set(account);
        Mockito.verify(shusshaRepository, Mockito.times(1)).find(accountId, date);
        Mockito.verify(shusshaRepository, Mockito.times(1)).add(shussha);
    }

    @Test
    public void testAddUpdate() {
        // setup
        final Integer accountId = 1;
        final LocalDate date = LocalDate.now();
        final ShusshaStatus status = ShusshaStatus.DONE;
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        shussha.setDate(date);
        shussha.setStatus(status);

        Shussha existedShussha = new Shussha();
        existedShussha.setShusshaId(10);
        existedShussha.setAccountId(accountId);
        existedShussha.setDate(date);
        existedShussha.setStatus(ShusshaStatus.TOBE);

        final int shusshaCount = 3;
        Account account = new Account();
        account.setShusshaCount(shusshaCount);

        Mockito.doReturn(existedShussha).when(shusshaRepository).find(accountId, date);
        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        // when
        target.add(shussha);

        // then
        assertEquals(shusshaCount + 1, account.getShusshaCount());
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
        Mockito.verify(accountRepository, Mockito.times(1)).set(account);
        Mockito.verify(shusshaRepository, Mockito.times(1)).find(accountId, date);
        Mockito.verify(shusshaRepository, Mockito.times(1)).set(shussha);
    }

    @Test
    public void testAddNewWithTobe() {
        // setup
        final Integer accountId = 1;
        final LocalDate date = LocalDate.now();
        final ShusshaStatus status = ShusshaStatus.TOBE;
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        shussha.setDate(date);
        shussha.setStatus(status);

        final int shusshaCount = 3;
        Account account = new Account();
        account.setShusshaCount(shusshaCount);

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);

        // when
        target.add(shussha);

        // then
        assertEquals(shusshaCount, account.getShusshaCount());
        Mockito.verify(accountRepository, Mockito.times(0)).findOne(accountId);
        Mockito.verify(accountRepository, Mockito.times(0)).set(account);
        Mockito.verify(shusshaRepository, Mockito.times(1)).find(accountId, date);
        Mockito.verify(shusshaRepository, Mockito.times(1)).add(shussha);
    }

    @Test
    public void testRemove() {
        // setup
        final Integer shusshaId = 2;
        final Integer accountId = 1;
        final LocalDate date = LocalDate.now();
        Shussha shussha = new Shussha();
        shussha.setShusshaId(shusshaId);
        shussha.setAccountId(accountId);

        final int shusshaCount = 3;
        Account account = new Account();
        account.setShusshaCount(shusshaCount);

        Mockito.doReturn(account).when(accountRepository).findOne(accountId);
        Mockito.doReturn(shussha).when(shusshaRepository).find(accountId, date);

        // when
        target.remove(accountId, date);

        // then
        assertEquals(shusshaCount - 1, account.getShusshaCount());
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(accountId);
        Mockito.verify(accountRepository, Mockito.times(1)).set(account);
        Mockito.verify(shusshaRepository, Mockito.times(1)).find(accountId, date);
        Mockito.verify(shusshaRepository, Mockito.times(1)).remove(shussha);
    }

}
