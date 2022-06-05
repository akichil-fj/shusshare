package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Account;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final SqlSession sqlSession;

    public AccountRepositoryImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Account findOne(Integer id) {
        return null;
    }

    @Override
    public void add(Account account) {

    }

    @Override
    public void set(Account account) {

    }

    @Override
    public void remove(Account account) {

    }

}
