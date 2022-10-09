package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;

import java.util.List;

public interface RecruitmentService {

    List<RecruitmentDetail> find(Integer accountId);

    RecruitmentDetail get(Integer recruitmentId, Integer accountId);

    void add(Recruitment recruitment);

    void set(Recruitment recruitment);

    void remove(Integer recruitmentId);

    void addParticipants(Integer recruitmentId, List<Integer> accountIds);

    void removeParticipants(Integer recruitmentId, List<Integer> accountIds);

}
