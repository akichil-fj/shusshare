package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.FriendStatus;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.repository.dbunitUtil.DbTestExecutionListener;
import net.akichil.shusshare.repository.dbunitUtil.DbUnitUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

            assertEquals(4, findResults.size());
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
        }

        /**
         * あるユーザのフレンドを検索
         * フォローしている人
         */
        @Test
        public void testFindFriendFromUser() {
            final Integer id = 2;

            List<FriendDetail> findResults = target.findFriendFromUser(id);

            assertEquals(3, findResults.size());
            FriendDetail result0 = findResults.get(0);
            assertEquals(3, result0.getAccountId());
            assertEquals("test_piyo", result0.getUserId());
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
        }
    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class InsertTest {

        private static final String INSERT_DATA_PATH = "src/test/resources/testdata/insert";

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

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class UpdateTest {

        private static final String UPDATE_DATA_PATH = "src/test/resources/testdata/update";

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

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class DeleteTest {

        private static final String DELETE_DATA_PATH = "src/test/resources/testdata/delete";

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

    }

}
