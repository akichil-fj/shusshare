package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;

import java.util.List;

public interface RecruitmentRepository {

    List<RecruitmentDetail> findList(Integer accountId);

    RecruitmentDetail findOne(Integer recruitmentId, Integer accountId);

    void add(Recruitment recruitment);

    void set(Recruitment recruitment);

    void remove(Integer recruitmentId);

}
