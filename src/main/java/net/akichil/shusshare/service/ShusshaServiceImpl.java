package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Account;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.entity.ShusshaList;
import net.akichil.shusshare.entity.ShusshaStatus;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.repository.ShusshaRepository;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.service.exception.DataNotUpdatedException;
import net.akichil.shusshare.service.exception.IllegalDateRegisterException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShusshaServiceImpl implements ShusshaService {

    private final ShusshaRepository shusshaRepository;

    private final AccountRepository accountRepository;

    public ShusshaServiceImpl(ShusshaRepository shusshaRepository, AccountRepository accountRepository) {
        this.shusshaRepository = shusshaRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @Override
    public ShusshaList list(Integer accountId) {
        ShusshaList shusshaList = new ShusshaList();
        List<Shussha> shusshas = shusshaRepository.find(accountId);

        shusshaList.setPastShussha(shusshas.stream()
                .filter(s -> s.getDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Shussha::getDate).reversed())
                .collect(Collectors.toList()));

        shusshaList.setFutureShussha(shusshas.stream()
                .filter(s -> s.getDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(Shussha::getDate))
                .collect(Collectors.toList()));

        return shusshaList;
    }

    @Transactional
    @Override
    public Shussha get(Integer accountId, LocalDate date) {
        return shusshaRepository.find(accountId, LocalDate.now());
    }

    @Transactional
    @Override
    public void add(Shussha shussha) {
        // すでに登録済みの情報があるか？
        int shusshaId = -1;
        int lockVersion = 0;
        try {
            Shussha existShussha = shusshaRepository.find(shussha.getAccountId(), shussha.getDate());
            if (shussha.getStatus() == existShussha.getStatus()) {
                // 更新要素がない
                throw new DataNotUpdatedException();
            }
            shusshaId = existShussha.getShusshaId();
            lockVersion = existShussha.getLockVersion();
        } catch (ResourceNotFoundException ignored) {
            // 登録されていなかった
        }

        // 予定なのに今日以前
        if (shussha.getStatus() == ShusshaStatus.TOBE && !shussha.getDate().isAfter(LocalDate.now())) {
            throw new IllegalDateRegisterException();
        }

        // すでに登録済みなら更新、なければ登録
        if (shusshaId != -1) {
            shussha.setShusshaId(shusshaId);
            shussha.setLockVersion(lockVersion);
            shusshaRepository.set(shussha);
        } else {
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

    @Transactional
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
