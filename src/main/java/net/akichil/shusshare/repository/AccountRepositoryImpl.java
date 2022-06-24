package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountSelector;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
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
    public List<Account> findList(AccountSelector selector) {
        return sqlSession.getMapper(AccountMapper.class).findList(selector);
    }

    @Override
    public Account findOne(Integer id) {
        Account account = sqlSession.getMapper(AccountMapper.class).findOne(id);
        if (account == null) {
            throw new ResourceNotFoundException();
        }
        return account;
    }

    @Override
    public Account findOne(String userId) {
        Account account = sqlSession.getMapper(AccountMapper.class).findOneByUserId(userId);
        if (account == null) {
            throw new ResourceNotFoundException();
        }
        return account;
    }

    @Override
    public void add(Account account) {
        sqlSession.getMapper(AccountMapper.class).add(account);
    }

    @Override
    public void set(Account account) {
        final int affected = sqlSession.getMapper(AccountMapper.class).set(account);
        if (affected != 1) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public void remove(Account account) {
        final int affected = sqlSession.getMapper(AccountMapper.class).remove(account);
        if (affected != 1) {
            throw new ResourceNotFoundException();
        }
    }

}
