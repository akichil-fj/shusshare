package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.UserSelector;

import java.util.List;

public interface FriendRepository {

    List<Friend> findAllUser(UserSelector selector);

    List<Friend> findFriend(Integer id);

    List<Friend> findGoOfficeFriend(Integer id);

    Friend findOne(Integer id);

    void add(Friend friend);

    void set(Friend friend);

    void remove(Friend friend);

}
