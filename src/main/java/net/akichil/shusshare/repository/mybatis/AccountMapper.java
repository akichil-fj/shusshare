package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.UserSelector;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {

    List<Account> findAll(UserSelector selector);

    List<Account> findFriends(Long id);

    Account findOne(Long id);

    int add(Account account);

    int set(Account account);

    int remove(Account account);

}
