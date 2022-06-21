package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.FriendList;
import net.akichil.shusshare.entity.UserSelector;

import java.util.List;

public interface FriendService {

    List<FriendDetail> findAllUser(UserSelector selector);

    FriendList findFriends(Integer id);

    List<FriendDetail> findGoOfficeFriend(Integer id);

    FriendDetail findFriendByAccountId(Integer accountId, Integer accountIdFrom);

    FriendDetail findFriendByUserId(String userId, Integer accountIdFrom);

    void add(Friend friend);

    void set(Friend friend);

    void remove(Integer accountId, Integer accountIdFrom);

    void request(Integer accountId, Integer accountIdFrom);

    void allow(Integer accountId, Integer accountIdFrom);

}
