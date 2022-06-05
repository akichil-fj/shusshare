package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.FriendStatus;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.repository.dbunitUtil.DbTestExecutionListener;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ShusshareApplication.class)
public class FriendRepositoryImplDbUnitTest {

    @Autowired
    private FriendRepositoryImpl target;

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

}
