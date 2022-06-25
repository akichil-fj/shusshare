package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.entity.ShusshaStatus;
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
    public Shussha get(Integer accountId, LocalDate date) {
        return shusshaRepository.find(accountId, LocalDate.now());
    }

    @Override
    public void add(Shussha shussha) {
        // すでに登録済みの情報があるか？
        int shusshaId = -1;
        Shussha existShussha = shusshaRepository.find(shussha.getAccountId(), shussha.getDate());
        if (existShussha != null) {
            shusshaId = existShussha.getShusshaId();
        }
        // すでに登録済みなら更新、なければ登録
        if (shusshaId != -1) {
            shusshaRepository.set(shussha);
        } else {
            shussha.setShusshaId(shusshaId);
            shusshaRepository.add(shussha);
        }

        // 出社回数更新(+1)
        // DONEなら+1
        if (shussha.getStatus() == ShusshaStatus.DONE) {
            Account account = accountRepository.findOne(shussha.getAccountId());
            account.setShusshaCount(account.getShusshaCount() + 1);
            accountRepository.set(account);
        }
    }

    @Override
    public void remove(Integer accountId, LocalDate date) {
        Shussha shussha = shusshaRepository.find(accountId, date);
        shusshaRepository.remove(shussha);

        // 出社回数更新(-1)
        Account account = accountRepository.findOne(shussha.getAccountId());
        account.setShusshaCount(account.getShusshaCount() - 1);
        accountRepository.set(account);
    }
}
