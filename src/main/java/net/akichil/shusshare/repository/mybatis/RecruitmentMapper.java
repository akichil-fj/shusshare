package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.entity.RecruitmentSelector;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecruitmentMapper {

    List<RecruitmentDetail> findList(RecruitmentSelector selector);

    RecruitmentDetail findOne(@Param("recruitmentId")Integer recruitmentId, @Param("accountId") Integer accountId);

    Recruitment get(Integer recruitmentId);

    int add(Recruitment recruitment);

    int addParticipants(@Param("recruitmentId") Integer recruitmentId,
                        @Param("accountIds") List<Integer> accountIds);

    int set(Recruitment recruitment);

    int remove(Recruitment recruitment);

    int removeParticipants(@Param("recruitmentId") Integer recruitmentId,
                           @Param("accountIds") List<Integer> accountIds);

}
