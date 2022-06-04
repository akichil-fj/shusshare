package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.repository.mybatis.AccountMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final SqlSession sqlSession;

    public AccountRepositoryImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<Account> findAll(UserSelector selector) {
        return sqlSession.getMapper(AccountMapper.class).findAll(selector);
    }

    @Override
    public List<Account> findFriend(Integer id) {
        return null;
    }

    @Override
    public List<Account> findGoOfficeFriend(Integer id) {
        return null;
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
