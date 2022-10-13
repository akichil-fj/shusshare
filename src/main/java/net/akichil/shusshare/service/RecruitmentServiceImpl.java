package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.FriendRepository;
import net.akichil.shusshare.repository.RecruitmentRepository;
import net.akichil.shusshare.repository.ShusshaRepository;
import net.akichil.shusshare.service.exception.NoAccessResourceException;
import net.akichil.shusshare.service.exception.ParticipantsOverCapacityException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    private final FriendRepository friendRepository;

    private final ShusshaRepository shusshaRepository;

    public RecruitmentServiceImpl(RecruitmentRepository recruitmentRepository, FriendRepository friendRepository, ShusshaRepository shusshaRepository) {
        this.recruitmentRepository = recruitmentRepository;
        this.friendRepository = friendRepository;
        this.shusshaRepository = shusshaRepository;
    }

    @Transactional
    @Override
    public List<RecruitmentDetail> find(RecruitmentSelector selector) {
        List<RecruitmentDetail> recruitments = recruitmentRepository.findList(selector);
        recruitments.forEach(r -> setParticipate(r, selector.getAccountId()));
        return recruitments;
    }

    @Transactional
    @Override
    public RecruitmentDetail get(Integer recruitmentId, Integer accountId) {
        RecruitmentDetail recruitment = recruitmentRepository.findOne(recruitmentId, accountId);
        setParticipate(recruitment, accountId);
        return recruitment;
    }

    @Transactional
    @Override
    public void add(Recruitment recruitment) {
        // 出社の登録者と募集の作成者が一致するか
        Shussha shussha = shusshaRepository.get(recruitment.getShusshaId());
        if (!shussha.getAccountId().equals(recruitment.getCreatedBy())) {
            throw new NoAccessResourceException();
        }
        // 締切日時を出社日に合わせる
        if (recruitment.getDeadline() != null) {
            recruitment.setDeadline(LocalDateTime.of(shussha.getDate(), recruitment.getDeadline().toLocalTime()));
        }
        recruitmentRepository.add(recruitment);
    }

    @Transactional
    @Override
    public void set(Recruitment recruitment) {
        // 出社の登録者と募集の作成者が一致するか
        Recruitment existedRecruitment = recruitmentRepository.findOne(recruitment.getRecruitmentId());
        Shussha shussha = shusshaRepository.get(existedRecruitment.getShusshaId());
        if (!shussha.getAccountId().equals(recruitment.getCreatedBy())) {
            throw new NoAccessResourceException();
        }
        // 締切日時を出社日に合わせる
        if (recruitment.getDeadline() != null) {
            recruitment.setDeadline(LocalDateTime.of(shussha.getDate(), recruitment.getDeadline().toLocalTime()));
        }
        recruitmentRepository.set(recruitment);
    }

    @Transactional
    @Override
    public void cancel(Integer recruitmentId, Integer accountId) {
        Recruitment recruitment = recruitmentRepository.findOne(recruitmentId);
        if (!recruitment.getCreatedBy().equals(accountId)) {
            throw new NoAccessResourceException();
        }
        recruitment.setStatus(RecruitmentStatus.CANCELED);
        recruitmentRepository.set(recruitment);
    }

    @Transactional
    @Override
    public void addParticipants(Integer recruitmentId, List<Integer> accountIds) {
        Recruitment recruitment = recruitmentRepository.findOne(recruitmentId);
        for (Integer accountId : accountIds) {
            // 作成者をフォローしているかどうか
            List<FriendDetail> friendFromUser = friendRepository.findFriendFromUser(accountId);
            friendFromUser.stream().filter(f -> f.getAccountId().equals(recruitment.getCreatedBy()))
                    .filter(f -> f.getStatus() == FriendStatus.FOLLOWED)
                    .findAny().orElseThrow(NoAccessResourceException::new);
        }
        // 追加する参加者が最大人数より多い
        if (recruitment.getCapacity() != null
                && recruitment.getCapacity() < recruitment.getParticipantCount() + accountIds.size()) {
            throw new ParticipantsOverCapacityException();
        }

        recruitmentRepository.addParticipants(recruitmentId, accountIds);
    }

    @Transactional
    @Override
    public void removeParticipants(Integer recruitmentId, List<Integer> accountIds) {
        recruitmentRepository.removeParticipants(recruitmentId, accountIds);
    }

    void setParticipate(RecruitmentDetail recruitmentDetail, Integer accountId) {
        setCanParticipate(recruitmentDetail, accountId);
        setIsParticipate(recruitmentDetail, accountId);
    }

    void setCanParticipate(RecruitmentDetail r, Integer accountId) {
        boolean canParticipate = true;
        if (r.getDeadline() != null && r.getDeadline().isBefore(LocalDateTime.now()))
            canParticipate = false;
        else if (r.getCapacity() != null && r.getCapacity() <= r.getParticipantCount())
            canParticipate = false;
        else if (r.getStatus() != RecruitmentStatus.OPENED)
            canParticipate = false;
        else if (r.getCreatedBy().equals(accountId))
            canParticipate = false;
        r.setCanParticipate(canParticipate);
    }

    void setIsParticipate(RecruitmentDetail r, Integer accountId) {
        boolean isParticipate = r.getParticipants().stream().anyMatch(p -> p.getAccountId().equals(accountId)); // 自分が参加している
        r.setIsParticipate(isParticipate);
    }

}
