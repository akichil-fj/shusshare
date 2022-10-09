package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.repository.RecruitmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    public RecruitmentServiceImpl(RecruitmentRepository recruitmentRepository) {
        this.recruitmentRepository = recruitmentRepository;
    }

    @Transactional
    @Override
    public List<RecruitmentDetail> find(Integer accountId) {
        return recruitmentRepository.findList(accountId);
    }

    @Transactional
    @Override
    public RecruitmentDetail get(Integer recruitmentId, Integer accountId) {
        return recruitmentRepository.findOne(recruitmentId, accountId);
    }

    @Transactional
    @Override
    public void add(Recruitment recruitment) {
        recruitmentRepository.add(recruitment);
    }

    @Transactional
    @Override
    public void set(Recruitment recruitment) {
        recruitmentRepository.set(recruitment);
    }

    @Transactional
    @Override
    public void remove(Integer recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findOne(recruitmentId);
        recruitmentRepository.remove(recruitment);
    }

    @Transactional
    @Override
    public void addParticipants(Integer recruitmentId, List<Integer> accountIds) {
        recruitmentRepository.addParticipants(recruitmentId, accountIds);
    }

    @Transactional
    @Override
    public void removeParticipants(Integer recruitmentId, List<Integer> accountIds) {
        recruitmentRepository.removeParticipants(recruitmentId, accountIds);
    }

}
