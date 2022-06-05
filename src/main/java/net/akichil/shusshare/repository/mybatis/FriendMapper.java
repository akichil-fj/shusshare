package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.UserSelector;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper {

    List<FriendDetail> findAllUser(UserSelector selector);

    List<FriendDetail> findFriendsFromUser(Integer id);

    List<FriendDetail> findFriendsToUser(Integer id);

    List<FriendDetail> findGoOfficeFriends(Integer id);

    int add(Friend friend);

    int update(Friend friend);

    int remove(Friend friend);

}
