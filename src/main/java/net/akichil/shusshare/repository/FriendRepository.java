package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.ShusshaFriends;
import net.akichil.shusshare.entity.UserSelector;

import java.time.LocalDate;
import java.util.List;

public interface FriendRepository {

    List<FriendDetail> findAllUser(UserSelector selector);

    List<FriendDetail> findFriendFromUser(Integer id);

    List<FriendDetail> findFriendsToUser(Integer id);

    List<ShusshaFriends> findGoOfficeFriend(Integer id, LocalDate startDate, LocalDate endDate);

    FriendDetail findFriendByAccountId(Integer accountId, Integer accountIdFrom);

    FriendDetail findFriendByAccountId(String userId, Integer accountIdFrom);

    void add(Friend friend);

    void set(Friend friend);

    void remove(Friend friend);

}
