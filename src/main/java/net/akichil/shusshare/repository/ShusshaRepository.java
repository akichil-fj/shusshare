package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Shussha;

import java.time.LocalDate;

public interface ShusshaRepository {

    Shussha find(Integer accountId, LocalDate date);

    void add(Shussha shussha);

    void remove(Shussha shussha);

}
