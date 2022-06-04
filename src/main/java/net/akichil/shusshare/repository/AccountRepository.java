package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.UserSelector;

import java.util.List;

public interface AccountRepository {

    List<Account> findAll(UserSelector selector);

    List<Account> findFriend(Integer id);

    List<Account> findGoOfficeFriend(Integer id);

    Account findOne(Integer id);

    void add(Account account);

    void set(Account account);

    void remove(Account account);

}
