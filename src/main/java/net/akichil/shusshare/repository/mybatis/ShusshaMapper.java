package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Shussha;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ShusshaMapper {

    Shussha find(@Param("accountId") Integer accountId, @Param("date") LocalDate date);

    void add(Shussha shussha);

    int remove(Shussha shussha);

    int set(Shussha shussha);

}
