package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Shussha;

public interface ShusshaService {

    Shussha getToday(Integer accountId);

    void addToday(Shussha shussha);

    void removeToday(Integer accountId);

}
