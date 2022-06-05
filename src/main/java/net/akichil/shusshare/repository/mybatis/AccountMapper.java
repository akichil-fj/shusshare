package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {

    Account findOne(Integer id);

    int add(Account account);

    int set(Account account);

    int remove(Account account);

}
