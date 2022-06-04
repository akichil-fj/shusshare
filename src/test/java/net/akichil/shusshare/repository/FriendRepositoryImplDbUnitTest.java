package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.Friend;
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

            final List<Friend> findResults = target.findAllUser(selector);

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

            final List<Friend> findResults = target.findAllUser(selector);

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

            final List<Friend> findResults = target.findAllUser(selector);

            assertEquals(2, findResults.size());
        }

    }

}
