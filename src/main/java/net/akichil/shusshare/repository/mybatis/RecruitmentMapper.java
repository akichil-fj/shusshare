package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecruitmentMapper {

    List<RecruitmentDetail> findList(@Param("accountId") Integer accountId,
                                              @Param("createdById") Integer createdAccountId,
                                              @Param("shusshaId") Integer shusshaId);

    RecruitmentDetail findOne(@Param("recruitmentId")Integer recruitmentId, @Param("accountId") Integer accountId);

    int add(Recruitment recruitment);

    int addParticipants(Integer recruitmentId, List<Integer> accountIds);

    int set(Recruitment recruitment);

    int remove(Integer recruitmentId);

    int removeParticipants(Integer recruitmentId, List<Integer> accountIds);

}
