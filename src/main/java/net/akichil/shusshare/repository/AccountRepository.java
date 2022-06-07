package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountSelector;

import java.util.List;

public interface AccountRepository {

    List<Account> findList(AccountSelector selector);

    Account findOne(Integer id);

    void add(Account account);

    void set(Account account);

    void remove(Account account);

}
