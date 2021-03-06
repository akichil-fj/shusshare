package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.EditPassword;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.service.exception.PasswordNotMatchException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Account get(Integer accountId) {
        return accountRepository.findOne(accountId);
    }

    @Transactional
    @Override
    public Account get(String userId) {
        return accountRepository.findOne(userId);
    }

    @Transactional
    @Override
    public void add(Account account) {
        accountRepository.add(account);
    }

    @Transactional
    @Override
    public void set(Account account) {
        accountRepository.set(account);
    }

    @Transactional
    @Override
    public void remove(Integer accountId) {
        Account account = accountRepository.findOne(accountId);
        accountRepository.remove(account);
    }

    @Transactional
    @Override
    public void setPassword(Integer accountId, EditPassword password) {
        Account account = accountRepository.findOne(accountId);
        if (!passwordEncoder.matches(password.getOldPassword(), account.getPassword())) {
            throw new PasswordNotMatchException();
        }
        // パスワードをハッシュ化
        account.setPassword(passwordEncoder.encode(password.getNewPassword()));
        accountRepository.set(account);
    }

}
