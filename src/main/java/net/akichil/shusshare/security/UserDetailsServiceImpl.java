package net.akichil.shusshare.security;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.AccountSelector;
import net.akichil.shusshare.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public LoginUser loadUserByUsername(String userId) throws UsernameNotFoundException {
        AccountSelector selector = new AccountSelector();
        selector.setUserId(userId);

        List<Account> accounts = accountRepository.findList(selector);

        if (accounts.size() != 1) {
            // アカウントが一意に決まらない
            throw new UsernameNotFoundException("User not found.");
        }
        Account account = accounts.get(0);

        return new LoginUser(account.getAccountId(), account.getUserId(), account.getPassword(), AccountRole.USER.getGradAuthority());
    }

}
