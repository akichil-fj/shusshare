package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.FriendStatus;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.repository.dbunitUtil.DbTestExecutionListener;
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
public class RecruitmentRepositoryImplTest {

    @Autowired
    private RecruitmentRepository target;

    @Autowired
    private DataSource dataSource;

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class FindTest {

        @Test
        public void testFindAll() {
            final Integer accountId = 2;

            List<RecruitmentDetail> results = target.findList(accountId);

            assertEquals(2, results.size());
            assertEquals(3, results.get(1).getParticipants().size());
            assertEquals(4, results.get(0).getParticipants().get(0).getAccountId());
            assertEquals(3, results.get(0).getRecruitmentId());
            assertEquals("test_user4", results.get(0).getCreatedFriend().getUserId());
        }

        @Test
        public void testFindOne() {
            final Integer recruitmentId = 2;
            final Integer accountId = 1;

            RecruitmentDetail result = target.findOne(recruitmentId, accountId);

            assertEquals("募集byふが", result.getTitle());
            assertEquals("ふが山フガ子", result.getCreatedFriend().getUserName());
            assertEquals(0, result.getParticipants().size());
        }

        @Test
        public void testFindOneNotFollowed() {
            final Integer recruitmentId = 4;
            final Integer accountId = 1;

            RecruitmentDetail result = target.findOne(recruitmentId, accountId);

            assertEquals("募集テスト4", result.getTitle());
            assertEquals(FriendStatus.NONE, result.getCreatedFriend().getStatus());
        }

    }
}
