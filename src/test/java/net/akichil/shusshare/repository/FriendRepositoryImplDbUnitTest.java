package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.*;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShusshareApplication.class)
public class FriendRepositoryImplDbUnitTest {

    @Autowired
    private FriendRepositoryImpl target;

    @Autowired
    private DataSource dataSource;

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class FindTest {

        /**
         * 全ユーザを引っ張ってくるテスト
         */
        @Test
        public void testFindAll() {
            final UserSelector selector = new UserSelector();
            selector.setAccountIdFrom(1);

            final List<FriendDetail> findResults = target.findAllUser(selector);

            assertEquals(3, findResults.size());
        }

        /**
         * ユーザ名で検索
         */
        @Test
        public void testFindByUsername() {
            final String userName = "山";
            final UserSelector selector = new UserSelector();
            selector.setAccountIdFrom(1);
            selector.setUserName(userName);

            final List<FriendDetail> findResults = target.findAllUser(selector);

            assertEquals(2, findResults.size());
            assertEquals(AccountStatus.NORMAL, findResults.get(0).getAccountStatus());
        }

        /**
         * ユーザIDで検索
         */
        @Test
        public void testFindByUserId() {
            final String userName = "user";
            final UserSelector selector = new UserSelector();
            selector.setAccountIdFrom(1);
            selector.setUserName(userName);

            final List<FriendDetail> findResults = target.findAllUser(selector);

            assertEquals(2, findResults.size());
            assertEquals(AccountStatus.PRIVATE, findResults.get(1).getAccountStatus());
        }

        /**
         * あるユーザのフレンドを検索
         * フォローしている人
         */
        @Test
        public void testFindFriendFromUser() {
            final Integer id = 2;

            List<FriendDetail> findResults = target.findFriendFromUser(id);

            assertEquals(2, findResults.size());
            FriendDetail result0 = findResults.get(0);
            assertEquals(1, result0.getAccountId());
            assertEquals("test_hoge", result0.getUserId());
            assertEquals(AccountStatus.NORMAL, result0.getAccountStatus());
        }

        /**
         * あるユーザのフレンドを検索
         * フォロワー
         */
        @Test
        public void testFindFriendToUser() {
            final Integer id = 2;

            List<FriendDetail> findResults = target.findFriendsToUser(id);

            assertEquals(1, findResults.size());
            FriendDetail result0 = findResults.get(0);
            assertEquals(1, result0.getAccountId());
            assertEquals("ほげ山ほげお", result0.getUserName());
            assertEquals(FriendStatus.FOLLOWED, result0.getStatus());
            assertEquals(AccountStatus.NORMAL, result0.getAccountStatus());
        }

        /**
         * account_idでフレンド検索
         */
        @Test
        public void testFindFriendById() {
            final Integer accountId = 2;
            final Integer accountIdFrom = 1;

            FriendDetail findResult = target.findFriendByAccountId(accountId, accountIdFrom);

            assertEquals(accountId, findResult.getAccountId());
            assertEquals(FriendStatus.FOLLOWED, findResult.getStatus());
            assertEquals(AccountStatus.NORMAL, findResult.getAccountStatus());
        }

        /**
         * account_idが存在しない
         */
        @Test
        public void testFindFriendNotFound() {
            final Integer accountId = 100;
            final Integer accountIdFrom = 1;

            assertThrows(ResourceNotFoundException.class, () -> target.findFriendByAccountId(accountId, accountIdFrom));
        }

        /**
         * 削除済みのaccount_idを検索
         */
        @Test
        public void testFindFriendDeleted() {
            final Integer accountId = 3;
            final Integer accountIdFrom = 1;

            assertThrows(ResourceNotFoundException.class, () -> target.findFriendByAccountId(accountId, accountIdFrom));
        }

        /**
         * user_idでフレンド検索
         */
        @Test
        public void testFindFriendByUserId() {
            final String userId = "test_fuga";
            final Integer accountIdFrom = 1;

            FriendDetail findResult = target.findFriendByAccountId(userId, accountIdFrom);

            assertEquals(userId, findResult.getUserId());
            assertEquals(FriendStatus.FOLLOWED, findResult.getStatus());
        }

        /**
         * account_idが存在しない
         */
        @Test
        public void testFindFriendNotFoundByUserId() {
            final String userId = "xxxxxxxxxxxx";
            final Integer accountIdFrom = 1;

            assertThrows(ResourceNotFoundException.class, () -> target.findFriendByAccountId(userId, accountIdFrom));
        }

        /**
         * 削除済みのaccount_idを検索
         */
        @Test
        public void testFindFriendDeletedByUserId() {
            final String userId = "sssssssssssss";
            final Integer accountIdFrom = 1;

            assertThrows(ResourceNotFoundException.class, () -> target.findFriendByAccountId(userId, accountIdFrom));
        }

        @Test
        public void testFindGoOfficeFriend() {
            final Integer id = 2;
            final LocalDate date = LocalDate.of(2022, 6, 6);

            List<ShusshaFriends> findResults = target.findGoOfficeFriend(id, date, date);

            assertEquals(1, findResults.size());
            assertEquals(1, findResults.get(0).getFriends().size());
            assertEquals(LocalDate.of(2022, 6, 6), findResults.get(0).getDate());
            assertEquals(4, findResults.get(0).getFriends().get(0).getAccountId());
        }

        @Test
        public void testFindGoOfficeFriendWithNullDate() {
            final Integer id = 2;
            final LocalDate date = LocalDate.of(2022, 6, 5);

            List<ShusshaFriends> findResults = target.findGoOfficeFriend(id, date, null);

            assertEquals(1, findResults.size());
            assertEquals(1, findResults.get(0).getFriends().size());
            assertEquals(LocalDate.of(2022, 6, 6), findResults.get(0).getDate());
            assertEquals(4, findResults.get(0).getFriends().get(0).getAccountId());
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
        public void testInsert() throws Exception {
            Friend insertData = new Friend();
            insertData.setAccountIdFrom(1);
            insertData.setAccountIdTo(3);
            insertData.setStatus(FriendStatus.FOLLOWED);

            target.add(insertData);

            DbUnitUtil.assertMutateResults(dataSource, "friend", INSERT_DATA_PATH,
                    "updated_at");
        }

        /**
         * 挿入失敗時のテスト
         * 主キーが重複
         */
        @Test
        public void testInsertFail() {
            Friend insertData = new Friend();
            insertData.setAccountIdFrom(1);
            insertData.setAccountIdTo(2);
            insertData.setStatus(FriendStatus.FOLLOWED);

            assertThrows(DuplicateKeyException.class, () -> target.add(insertData));
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
        public void testUpdate() throws Exception {
            Friend updateData = new Friend();
            updateData.setAccountIdFrom(2);
            updateData.setAccountIdTo(1);
            updateData.setStatus(FriendStatus.REJECTED);
            updateData.setLockVersion(0);

            target.set(updateData);

            DbUnitUtil.assertMutateResults(dataSource, "friend", UPDATE_DATA_PATH,
                    "updated_at");
        }

        /**
         * 更新失敗時のテスト
         */
        @Test
        public void testUpdateFail() {
            Friend updateData = new Friend();
            updateData.setAccountIdFrom(2);
            updateData.setAccountIdTo(7);
            updateData.setStatus(FriendStatus.FOLLOWED);
            updateData.setLockVersion(0);

            assertThrows(ResourceNotFoundException.class, () -> target.set(updateData));
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
        public void testDelete() throws Exception {
            Friend deleteData = new Friend();
            deleteData.setAccountIdFrom(2);
            deleteData.setAccountIdTo(4);
            deleteData.setLockVersion(0);

            target.remove(deleteData);

            DbUnitUtil.assertMutateResults(dataSource, "friend", DELETE_DATA_PATH,
                    "updated_at");
        }

        /**
         * 削除失敗時のテスト
         */
        @Test
        public void testDeleteFail() {
            Friend deleteData = new Friend();
            deleteData.setAccountIdFrom(1);
            deleteData.setAccountIdTo(5);
            deleteData.setLockVersion(0);

            assertThrows(ResourceNotFoundException.class, () -> target.remove(deleteData));
        }

    }

}
