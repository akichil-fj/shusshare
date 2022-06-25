package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Shussha;

import java.time.LocalDate;
import java.util.List;

public interface ShusshaRepository {

    List<Shussha> find(Integer accountId);

    Shussha find(Integer accountId, LocalDate date);

    void add(Shussha shussha);

    void remove(Shussha shussha);

    void set(Shussha shussha);

}
