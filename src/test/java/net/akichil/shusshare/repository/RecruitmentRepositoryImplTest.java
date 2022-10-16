package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.repository.mybatis.RecruitmentMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecruitmentRepositoryImplTest {
    
    @Mock
    private RecruitmentMapper recruitmentMapper;

    @Mock
    private SqlSession sqlSession;
    
    @InjectMocks
    private RecruitmentRepositoryImpl target;

    private AutoCloseable closeable;

    @BeforeEach
    public void beforeEach() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.doReturn(recruitmentMapper).when(sqlSession).getMapper(RecruitmentMapper.class);
    }

    @AfterEach
    public void afterEach() throws Exception {
        closeable.close();
    }

    @Test
    public void testAddParticipants() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);
        final Recruitment recruitment = Recruitment.builder()
                .recruitmentId(recruitmentId)
                .participantCount(0)
                .build();
        Mockito.doReturn(recruitment).when(recruitmentMapper).get(recruitmentId);
        Mockito.doReturn(1).when(recruitmentMapper).set(recruitment);
        ArgumentCaptor<Recruitment> captor = ArgumentCaptor.forClass(Recruitment.class);

        target.addParticipants(recruitmentId, accountIds);

        Mockito.verify(recruitmentMapper, Mockito.times(1)).get(recruitmentId);
        Mockito.verify(recruitmentMapper, Mockito.times(1)).addParticipants(recruitmentId, accountIds);
        Mockito.verify(recruitmentMapper, Mockito.times(1)).set(captor.capture());
        assertEquals(accountIds.size(), captor.getValue().getParticipantCount()); // 加算されているか
    }

    @Test
    public void testRemoveParticipants() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);
        final Recruitment recruitment = Recruitment.builder()
                .recruitmentId(recruitmentId)
                .participantCount(3)
                .build();
        Mockito.doReturn(recruitment).when(recruitmentMapper).get(recruitmentId);
        Mockito.doReturn(1).when(recruitmentMapper).set(recruitment);
        Mockito.doReturn(accountIds.size()).when(recruitmentMapper).removeParticipants(recruitmentId, accountIds);
        ArgumentCaptor<Recruitment> captor = ArgumentCaptor.forClass(Recruitment.class);

        target.removeParticipants(recruitmentId, accountIds);

        Mockito.verify(recruitmentMapper, Mockito.times(1)).get(recruitmentId);
        Mockito.verify(recruitmentMapper, Mockito.times(1)).removeParticipants(recruitmentId, accountIds);
        Mockito.verify(recruitmentMapper, Mockito.times(1)).set(captor.capture());
        assertEquals(1, captor.getValue().getParticipantCount()); // 減算されているか
    }
}
