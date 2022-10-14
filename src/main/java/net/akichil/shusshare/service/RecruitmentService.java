package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.entity.RecruitmentSelector;

import java.util.List;

public interface RecruitmentService {

    List<RecruitmentDetail> find(RecruitmentSelector selector);

    RecruitmentDetail get(Integer recruitmentId, Integer accountId);

    void add(Recruitment recruitment);

    void set(Recruitment recruitment);

    void cancel(Integer recruitmentId, Integer accountId);

    void reopen(Integer recruitmentId, Integer accountId);

    void addParticipants(Integer recruitmentId, List<Integer> accountIds);

    void removeParticipants(Integer recruitmentId, List<Integer> accountIds);

}
