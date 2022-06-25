package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.repository.mybatis.ShusshaMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class ShusshaRepositoryImpl implements ShusshaRepository {

    private final SqlSession sqlSession;

    public ShusshaRepositoryImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Shussha find(Integer accountId, LocalDate date) {
        Shussha shussha = sqlSession.getMapper(ShusshaMapper.class).find(accountId, date);
        if (shussha == null) {
            throw new ResourceNotFoundException();
        }
        return shussha;
    }

    @Override
    public void add(Shussha shussha) {
        sqlSession.getMapper(ShusshaMapper.class).add(shussha);
    }

    @Override
    public void remove(Shussha shussha) {
        final int affected = sqlSession.getMapper(ShusshaMapper.class).remove(shussha);
        if (affected != 1) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void set(Shussha shussha) {
        final int affected = sqlSession.getMapper(ShusshaMapper.class).set(shussha);
        if (affected != 1) {
            throw new ResourceNotFoundException();
        }
    }

}
