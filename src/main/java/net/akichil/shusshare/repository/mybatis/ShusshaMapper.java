package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Shussha;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ShusshaMapper {

    List<Shussha> findAll(Integer accountId);

    Shussha find(@Param("accountId") Integer accountId, @Param("date") LocalDate date);

    Shussha get(Integer shusshaId);

    void add(Shussha shussha);

    int remove(Shussha shussha);

    int set(Shussha shussha);

}
