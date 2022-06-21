package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;

public interface AccountService {

    Account get(Integer accountId);

    Account get(String userId);

    void add(Account account);

    void set(Account account);

    void remove(Integer accountId);

}
