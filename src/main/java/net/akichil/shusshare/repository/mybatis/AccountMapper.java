package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountSelector;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {

    List<Account> findList(AccountSelector selector);

    Account findOne(Integer id);

    Account findOneByUserId(String userId);

    int add(Account account);

    int set(Account account);

    int remove(Account account);

}
