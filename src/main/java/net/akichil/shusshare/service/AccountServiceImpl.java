package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account get(Integer accountId) {
        return accountRepository.findOne(accountId);
    }

    @Override
    public Account get(String userId) {
        return accountRepository.findOne(userId);
    }

    @Override
    public void add(Account account) {
        accountRepository.add(account);
    }

    @Override
    public void set(Account account) {
        accountRepository.set(account);
    }

    @Override
    public void remove(Integer accountId) {
        Account account = accountRepository.findOne(accountId);
        accountRepository.remove(account);
    }

}
