package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Account;

public interface AccountRepository {

    Account findOne(Integer id);

    void add(Account account);

    void set(Account account);

    void remove(Account account);

}
