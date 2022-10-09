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

    Recruitment get(Integer recruitmentId);

    int add(Recruitment recruitment);

    int addParticipants(@Param("recruitmentId") Integer recruitmentId,
                        @Param("accountIds") List<Integer> accountIds);

    int set(Recruitment recruitment);

    int remove(Recruitment recruitment);

    int removeParticipants(@Param("recruitmentId") Integer recruitmentId,
                           @Param("accountIds") List<Integer> accountIds);

}
