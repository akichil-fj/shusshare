package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountStatus;
import net.akichil.shusshare.repository.dbunitUtil.DbTestExecutionListener;
import net.akichil.shusshare.repository.dbunitUtil.DbUnitUtil;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShusshareApplication.class)
public class AccountRepositoryImplDbUnitTest {

    @Autowired
    private AccountRepositoryImpl target;

    @Autowired
    private DataSource dataSource;

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

        /**
         * 削除済みユーザを検索
         */
        @Test
        public void testNotFoundDeleted() {
            final Integer accountId = 3;

            assertThrows(ResourceNotFoundException.class, () -> target.findOne(accountId));
        }

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class InsertTest {

        private static final String INSERT_DATA_PATH = "src/test/resources/testdata/insert";

        /**
         * 挿入成功時のテスト
         */
        @Test
        public void testInsertSuccess() throws Exception {
            Account insertData = new Account();
            insertData.setUserId("new_user");
            insertData.setUserName("新ユーザ");
            insertData.setPassword("password");
            insertData.setProfilePhotoUrl("https://hoge.com/sample.jpg");
            insertData.setStatus(AccountStatus.NORMAL);

            target.add(insertData);

            DbUnitUtil.assertMutateResults(dataSource, "account", INSERT_DATA_PATH, "account_id", "updated_at");
        }

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class UpdateTest {

        private static final String UPDATE_DATA_PATH = "src/test/resources/testdata/update";

        /**
         * 更新成功時のテスト
         */
        @Test
        public void testUpdateSuccess() throws Exception {
            Account updateData = new Account(2, "renew_user", "更新ユーザ", "password",
                    null, null, 0);

            target.set(updateData);

            DbUnitUtil.assertMutateResults(dataSource, "account", UPDATE_DATA_PATH, "updated_at");
        }

        /**
         * 更新失敗時のテスト(存在しないID)
         */
        @Test
        public void testUpdateFailIdNotExist() {
            Account updateData = new Account(99, "renew_user", "更新ユーザ", "password",
                    null, null, 0);

            assertThrows(ResourceNotFoundException.class, () -> target.set(updateData));
        }

        /**
         * 更新失敗時のテスト(lockVersionが一致しない)
         */
        @Test
        public void testUpdateFailLockVersionNotMatch() {
            Account updateData = new Account(2, "renew_user", "更新ユーザ", "password",
                    null, null, 3);

            assertThrows(ResourceNotFoundException.class, () -> target.set(updateData));
        }

        /**
         * 更新失敗時のテスト(userIdの一意制約違反)
         */
        @Test
        public void testUpdateFailUserIdUniqueKeyViolation() {
            Account updateData = new Account(2, "test_hoge", "更新ユーザ", "password",
                    null, null, 0);

            assertThrows(DuplicateKeyException.class, () -> target.set(updateData));
        }

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class DeleteTest {

        private static final String DELETE_DATA_PATH = "src/test/resources/testdata/delete";

        /**
         * 削除成功時のテスト
         */
        @Test
        public void testDeleteSuccess() throws Exception {
            Account deleteData = new Account();
            deleteData.setAccountId(4);
            deleteData.setLockVersion(0);

            target.remove(deleteData);

            DbUnitUtil.assertMutateResults(dataSource, "account", DELETE_DATA_PATH, "updated_at");
        }

        /**
         * 削除失敗時のテスト（UserIdが存在しない）
         */
        @Test
        public void testDeleteFailUserIdNotFound() {
            Account deleteData = new Account();
            deleteData.setAccountId(99);
            deleteData.setLockVersion(0);

            assertThrows(ResourceNotFoundException.class, () -> target.remove(deleteData));
        }

        /**
         * 削除失敗時のテスト（LockeVersionが一致しない）
         */
        @Test
        public void testDeleteFailLockVersionNotMatch() {
            Account deleteData = new Account();
            deleteData.setAccountId(4);
            deleteData.setLockVersion(10);

            assertThrows(ResourceNotFoundException.class, () -> target.remove(deleteData));
        }

    }

}