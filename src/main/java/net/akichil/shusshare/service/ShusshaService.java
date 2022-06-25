package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.entity.ShusshaList;

import java.time.LocalDate;

public interface ShusshaService {

    ShusshaList list(Integer accountId);

    Shussha get(Integer accountId, LocalDate date);

    void add(Shussha shussha);

    void remove(Integer accountId, LocalDate date);

}
