package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.*;

import java.util.List;

public interface FriendService {

    List<FriendDetail> findAllUser(UserSelector selector);

    FriendList findFriends(Integer id);

    List<ShusshaFriends> findGoOfficeFriend(Integer id);

    FriendDetail findFriendByAccountId(Integer accountId, Integer accountIdFrom);

    FriendDetail findFriendByUserId(String userId, Integer accountIdFrom);

    void add(Friend friend);

    void set(Friend friend);

    void remove(Integer accountId, Integer accountIdFrom);

    void request(Integer accountId, Integer accountIdFrom);

    void allow(Integer accountIdFrom, Integer accountIdTo);

    void deny(Integer accountIdFrom, Integer accountIdTo);

}
