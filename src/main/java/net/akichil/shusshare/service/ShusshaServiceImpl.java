package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.repository.ShusshaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ShusshaServiceImpl implements ShusshaService {

    private final ShusshaRepository shusshaRepository;

    private final AccountRepository accountRepository;

    public ShusshaServiceImpl(ShusshaRepository shusshaRepository, AccountRepository accountRepository) {
        this.shusshaRepository = shusshaRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Shussha getToday(Integer accountId) {
        return shusshaRepository.find(accountId, LocalDate.now());
    }

    @Override
    public void addToday(Shussha shussha) {
        shussha.setDate(LocalDate.now());
        shusshaRepository.add(shussha);

        // 出社回数更新(+1)
        Account account = accountRepository.findOne(shussha.getAccountId());
        account.setShusshaCount(account.getShusshaCount() + 1);
        accountRepository.set(account);
    }

    @Override
    public void removeToday(Integer accountId) {
        Shussha shussha = shusshaRepository.find(accountId, LocalDate.now());
        shusshaRepository.remove(shussha);

        // 出社回数更新(-1)
        Account account = accountRepository.findOne(shussha.getAccountId());
        account.setShusshaCount(account.getShusshaCount() - 1);
        accountRepository.set(account);
    }
}
