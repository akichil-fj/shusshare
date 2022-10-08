package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.repository.mybatis.RecruitmentMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecruitmentRepositoryImpl implements RecruitmentRepository {

    private final SqlSession sqlSession;

    public RecruitmentRepositoryImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<RecruitmentDetail> findList(Integer accountId) {
        List<RecruitmentDetail> results = sqlSession.getMapper(RecruitmentMapper.class).findList(accountId, null, null);
        results.forEach(r -> r.getParticipants().removeIf(p -> p.getAccountId() == null));  // JOINした結果、参加者0の場合項目がnullの参加者が生成されるので除外
        return results;
    }

    @Override
    public RecruitmentDetail findOne(Integer recruitmentId, Integer accountId) {
        RecruitmentDetail result = sqlSession.getMapper(RecruitmentMapper.class).findOne(recruitmentId, accountId);
        result.getParticipants().removeIf(p -> p.getAccountId() == null);  // JOINした結果、参加者0の場合項目がnullの参加者が生成されるので除外
        return result;
    }

    @Override
    public void add(Recruitment recruitment) {

    }

    @Override
    public void set(Recruitment recruitment) {

    }

    @Override
    public void remove(Integer recruitmentId) {

    }
}
