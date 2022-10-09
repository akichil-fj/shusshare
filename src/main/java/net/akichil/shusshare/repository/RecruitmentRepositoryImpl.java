package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Recruitment;
import net.akichil.shusshare.entity.RecruitmentDetail;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
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
        if (result == null) {
            throw new ResourceNotFoundException();
        }
        result.getParticipants().removeIf(p -> p.getAccountId() == null);  // JOINした結果、参加者0の場合項目がnullの参加者が生成されるので除外
        return result;
    }

    @Override
    public Recruitment findOne(Integer recruitmentId) {
        Recruitment result = sqlSession.getMapper(RecruitmentMapper.class).get(recruitmentId);
        if (result == null) {
            throw new ResourceNotFoundException();
        }
        return result;
    }

    @Override
    public void add(Recruitment recruitment) {
        sqlSession.getMapper(RecruitmentMapper.class).add(recruitment);
    }

    @Override
    public void set(Recruitment recruitment) {
        final int affected = sqlSession.getMapper(RecruitmentMapper.class).set(recruitment);
        if (affected == 0) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void remove(Recruitment recruitment) {
        final int affected = sqlSession.getMapper(RecruitmentMapper.class).remove(recruitment);
        if (affected == 0) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void addParticipants(Integer recruitmentId, List<Integer> accountIds) {
        Recruitment recruitment = this.findOne(recruitmentId);
        recruitment.setParticipantCount(recruitment.getParticipantCount() + accountIds.size()); //参加者を増やす
        this.set(recruitment);
        sqlSession.getMapper(RecruitmentMapper.class).addParticipants(recruitmentId, accountIds);
    }

    @Override
    public void removeParticipants(Integer recruitmentId, List<Integer> accountIds) {
        Recruitment recruitment = this.findOne(recruitmentId);
        recruitment.setParticipantCount(recruitment.getParticipantCount() - accountIds.size()); //参加者を減らす
        this.set(recruitment);
        final int affected = sqlSession.getMapper(RecruitmentMapper.class).removeParticipants(recruitmentId, accountIds);
        if (affected != accountIds.size()) {
            throw new ResourceNotFoundException();
        }
    }
}
