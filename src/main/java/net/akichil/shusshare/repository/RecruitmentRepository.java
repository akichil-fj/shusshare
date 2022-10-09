package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;

import java.util.List;

public interface RecruitmentRepository {

    List<RecruitmentDetail> findList(Integer accountId);

    RecruitmentDetail findOne(Integer recruitmentId, Integer accountId);

    Recruitment findOne(Integer recruitmentId);

    void add(Recruitment recruitment);

    void set(Recruitment recruitment);

    void remove(Recruitment recruitment);

    void addParticipants(Integer recruitmentId, List<Integer> accountIds);

    void removeParticipants(Integer recruitmentId, List<Integer> accountIds);

}
