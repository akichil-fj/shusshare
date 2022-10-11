package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.FriendRepository;
import net.akichil.shusshare.repository.RecruitmentRepository;
import net.akichil.shusshare.repository.ShusshaRepository;
import net.akichil.shusshare.service.exception.NoAccessResourceException;
import net.akichil.shusshare.service.exception.ParticipantsOverCapacityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class RecruitmentServiceImplTest {

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private ShusshaRepository shusshaRepository;

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
        final RecruitmentSelector selector = RecruitmentSelector.builder().build();
        List<Recruitment> recruitments = new ArrayList<>();
        Mockito.doReturn(recruitments).when(recruitmentRepository).findList(selector);

        List<RecruitmentDetail> results = target.find(selector);

        assertEquals(recruitments, results);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).findList(selector);
    }

    @Test
    public void testGet() {
        final Integer accountId = 1;
        final Integer recruitmentId = 2;
        final RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        recruitmentDetail.setStatus(RecruitmentStatus.OPENED);
        recruitmentDetail.setParticipants(List.of());
        Mockito.doReturn(recruitmentDetail).when(recruitmentRepository).findOne(recruitmentId, accountId);

        RecruitmentDetail result = target.get(recruitmentId, accountId);

        assertEquals(recruitmentDetail, result);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).findOne(recruitmentId, accountId);
    }

    @Test
    public void testAdd() {
        final Integer accountId = 2;
        final Integer shusshaId = 3;
        final Recruitment recruitment = Recruitment.builder()
                .title("test")
                .shusshaId(shusshaId)
                .createdBy(accountId)
                .deadline(LocalDateTime.of(2022, 1, 2, 3, 45))
                .build();
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        shussha.setDate(LocalDate.of(2022, 1, 3));
        Mockito.doReturn(shussha).when(shusshaRepository).get(shusshaId);

        target.add(recruitment);

        Mockito.verify(shusshaRepository, Mockito.times(1)).get(shusshaId);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).add(recruitment);
        assertEquals(LocalDateTime.of(2022, 1, 3, 3, 45), recruitment.getDeadline());
    }

    @Test
    public void testAddFailByDifferentAccountId() {
        final Integer accountId = 2;
        final Integer shusshaId = 3;
        final Recruitment recruitment = Recruitment.builder()
                .title("test")
                .shusshaId(shusshaId)
                .createdBy(100)
                .deadline(LocalDateTime.of(2022, 1, 2, 3, 45))
                .build();
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        Mockito.doReturn(shussha).when(shusshaRepository).get(shusshaId);

        assertThrows(NoAccessResourceException.class, () -> target.add(recruitment));

        Mockito.verify(shusshaRepository, Mockito.times(1)).get(shusshaId);
        Mockito.verify(recruitmentRepository, Mockito.times(0)).add(recruitment);
    }

    @Test
    public void testSet() {
        final Integer accountId = 2;
        final Integer shusshaId = 3;
        final Recruitment recruitment = Recruitment.builder()
                .title("test")
                .shusshaId(shusshaId)
                .createdBy(accountId)
                .deadline(LocalDateTime.of(2022, 1, 2, 3, 45))
                .build();
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        shussha.setDate(LocalDate.of(2022, 1, 3));
        Mockito.doReturn(shussha).when(shusshaRepository).get(shusshaId);

        target.set(recruitment);

        Mockito.verify(shusshaRepository, Mockito.times(1)).get(shusshaId);
        Mockito.verify(recruitmentRepository, Mockito.times(1)).set(recruitment);
        assertEquals(LocalDateTime.of(2022, 1, 3, 3, 45), recruitment.getDeadline());
    }

    @Test
    public void testSetFailByDifferentAccountId() {
        final Integer accountId = 2;
        final Integer shusshaId = 3;
        final Recruitment recruitment = Recruitment.builder()
                .title("test")
                .shusshaId(shusshaId)
                .createdBy(100)
                .deadline(LocalDateTime.of(2022, 1, 2, 3, 45))
                .build();
        Shussha shussha = new Shussha();
        shussha.setAccountId(accountId);
        Mockito.doReturn(shussha).when(shusshaRepository).get(shusshaId);

        assertThrows(NoAccessResourceException.class, () -> target.add(recruitment));

        Mockito.verify(shusshaRepository, Mockito.times(1)).get(shusshaId);
        Mockito.verify(recruitmentRepository, Mockito.times(0)).set(recruitment);
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
        Recruitment recruitment = Recruitment.builder().createdBy(1).capacity(4).participantCount(1).build();
        FriendDetail friendDetail1 = new FriendDetail();
        friendDetail1.setAccountId(1);
        friendDetail1.setStatus(FriendStatus.FOLLOWED);
        FriendDetail friendDetail2 = new FriendDetail();
        friendDetail2.setAccountId(1);
        friendDetail2.setStatus(FriendStatus.FOLLOWED);
        Mockito.doReturn(recruitment).when(recruitmentRepository).findOne(3);
        Mockito.doReturn(List.of(friendDetail1)).when(friendRepository).findFriendFromUser(4);
        Mockito.doReturn(List.of(friendDetail2)).when(friendRepository).findFriendFromUser(5);

        target.addParticipants(recruitmentId, accountIds);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).findOne(3);
        Mockito.verify(friendRepository, Mockito.times(2)).findFriendFromUser(any());
        Mockito.verify(recruitmentRepository, Mockito.times(1)).addParticipants(recruitmentId, accountIds);
    }

    @Test
    public void testAddParticipantsFailBecauseNotFollowed() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);
        Recruitment recruitment = Recruitment.builder().createdBy(1).capacity(4).participantCount(1).build();
        FriendDetail friendDetail1 = new FriendDetail();
        friendDetail1.setAccountId(1);
        friendDetail1.setStatus(FriendStatus.FOLLOWED);
        Mockito.doReturn(recruitment).when(recruitmentRepository).findOne(3);
        Mockito.doReturn(List.of(friendDetail1)).when(friendRepository).findFriendFromUser(4);
        Mockito.doReturn(List.of()).when(friendRepository).findFriendFromUser(5);

        assertThrows(NoAccessResourceException.class, () -> target.addParticipants(recruitmentId, accountIds));

        Mockito.verify(recruitmentRepository, Mockito.times(1)).findOne(3);
        Mockito.verify(friendRepository, Mockito.times(2)).findFriendFromUser(any());
        Mockito.verify(recruitmentRepository, Mockito.times(0)).addParticipants(recruitmentId, accountIds);
    }

    @Test
    public void testAddParticipantsFailBecauseCapacityOver() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);
        Recruitment recruitment = Recruitment.builder().createdBy(1).capacity(2).participantCount(1).build();
        FriendDetail friendDetail1 = new FriendDetail();
        friendDetail1.setAccountId(1);
        friendDetail1.setStatus(FriendStatus.FOLLOWED);
        FriendDetail friendDetail2 = new FriendDetail();
        friendDetail2.setAccountId(1);
        friendDetail2.setStatus(FriendStatus.FOLLOWED);
        Mockito.doReturn(recruitment).when(recruitmentRepository).findOne(3);
        Mockito.doReturn(List.of(friendDetail1)).when(friendRepository).findFriendFromUser(4);
        Mockito.doReturn(List.of(friendDetail2)).when(friendRepository).findFriendFromUser(5);

        assertThrows(ParticipantsOverCapacityException.class, () -> target.addParticipants(recruitmentId, accountIds));

        Mockito.verify(recruitmentRepository, Mockito.times(1)).findOne(3);
        Mockito.verify(friendRepository, Mockito.times(2)).findFriendFromUser(any());
        Mockito.verify(recruitmentRepository, Mockito.times(0)).addParticipants(recruitmentId, accountIds);
    }

    @Test
    public void testRemoveParticipants() {
        final Integer recruitmentId = 3;
        final List<Integer> accountIds = List.of(4, 5);

        target.removeParticipants(recruitmentId, accountIds);

        Mockito.verify(recruitmentRepository, Mockito.times(1)).removeParticipants(recruitmentId, accountIds);
    }

    @Test
    public void testSetCanParticipateOk() {
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        recruitmentDetail.setDeadline(LocalDateTime.now().plusHours(1));
        recruitmentDetail.setCapacity(3);
        recruitmentDetail.setParticipantCount(2);
        recruitmentDetail.setStatus(RecruitmentStatus.OPENED);

        target.setCanParticipate(recruitmentDetail);

        assertEquals(true, recruitmentDetail.getCanParticipate());
    }

    @Test
    public void testSetCanParticipateNgDeadline() {
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        recruitmentDetail.setDeadline(LocalDateTime.now().minusHours(1));
        recruitmentDetail.setStatus(RecruitmentStatus.OPENED);

        target.setCanParticipate(recruitmentDetail);

        assertEquals(false, recruitmentDetail.getCanParticipate());
    }

    @Test
    public void testSetCanParticipateNgCapacity() {
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        recruitmentDetail.setCapacity(4);
        recruitmentDetail.setParticipantCount(4);
        recruitmentDetail.setStatus(RecruitmentStatus.OPENED);

        target.setCanParticipate(recruitmentDetail);

        assertEquals(false, recruitmentDetail.getCanParticipate());
    }

    @Test
    public void testSetCanParticipateNgStatus() {
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        recruitmentDetail.setStatus(RecruitmentStatus.CLOSED);

        target.setCanParticipate(recruitmentDetail);

        assertEquals(false, recruitmentDetail.getCanParticipate());
    }

    @Test
    public void testSetIsParticipateTrue() {
        Integer accountId = 2;
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        Account account = new Account();
        account.setAccountId(accountId);
        recruitmentDetail.setParticipants(List.of(account));

        target.setIsParticipate(recruitmentDetail, accountId);

        assertEquals(true, recruitmentDetail.getIsParticipate());
    }

    @Test
    public void testSetIsParticipateFalse() {
        Integer accountId = 2;
        RecruitmentDetail recruitmentDetail = new RecruitmentDetail();
        Account account = new Account();
        account.setAccountId(5);
        recruitmentDetail.setParticipants(List.of(account));

        target.setIsParticipate(recruitmentDetail, accountId);

        assertEquals(false, recruitmentDetail.getIsParticipate());
    }

}
