package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountStatus;
import net.akichil.shusshare.repository.dbunitUtil.DbTestExecutionListener;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShusshareApplication.class)
public class AccountRepositoryImplDbUnitTest {

    @Autowired
    private AccountRepositoryImpl target;

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class FindTest {

        /**
         * アカウント取得テスト
         */
        @Test
        public void testFind() {
            final Integer accountId = 1;

            final Account findResult = target.findOne(accountId);

            assertEquals("test_hoge", findResult.getUserId());
            assertEquals("ほげ山ほげお", findResult.getUserName());
            assertEquals(AccountStatus.NORMAL, findResult.getStatus());
        }

        /**
         * アカウントが存在しない場合
         */
        @Test
        public void testNotFound() {
            final Integer accountId = 99;

            assertThrows(ResourceNotFoundException.class, () -> target.findOne(accountId));
        }

    }

}