package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Shussha;

import java.time.LocalDate;

public interface ShusshaService {

    Shussha get(Integer accountId, LocalDate date);

    void add(Shussha shussha);

    void remove(Integer accountId, LocalDate date);

}
