package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.repository.RecruitmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecruitmentServiceImplTest {

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @InjectMocks
    private RecruitmentServiceImpl target;

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
    public void testFind() {
        final Integer accountId = 1;
        List<Recruitment> recruitments = new ArrayList<>();
        Mockito.doReturn(recruitments).when(recruitmentRepository).findList(accountId);

        List<RecruitmentDetail> results = target.find(accountId);

        assertEquals(recruitments, results);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).findList(accountId);
    }

    @Test
    public void testGet() {
        final Integer accountId = 1;
        final Integer recruitmentId = 2;
        final RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        Mockito.doReturn(recruitmentDetail).when(recruitmentRepository).findOne(recruitmentId, accountId);

        RecruitmentDetail result = target.get(recruitmentId, accountId);

        assertEquals(recruitmentDetail, result);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).findOne(recruitmentId, accountId);
    }

    @Test
    public void testAdd() {
        final Recruitment recruitment = Recruitment.builder()
                .title("test")
                .build();

        target.add(recruitment);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).add(recruitment);
    }

    @Test
    public void testSet() {
        final Recruitment recruitment = Recruitment.builder()
                .recruitmentId(1)
                .title("test")
                .build();

        target.set(recruitment);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).set(recruitment);
    }

    @Test
    public void testRemove() {
        final Integer recruitmentId = 1;
        final Recruitment recruitment = Recruitment.builder()
                .recruitmentId(1)
                .build();
        Mockito.doReturn(recruitment).when(recruitmentRepository).findOne(recruitmentId);

        target.remove(recruitmentId);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).findOne(recruitmentId);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).remove(recruitment);
    }

    @Test
    public void testAddParticipants() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);

        target.addParticipants(recruitmentId, accountIds);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).addParticipants(recruitmentId, accountIds);
    }

    @Test
    public void testRemoveParticipants() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);

        target.removeParticipants(recruitmentId, accountIds);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).removeParticipants(recruitmentId, accountIds);
    }

}
