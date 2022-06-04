package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.UserSelector;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper {

    List<Friend> findAllUser(UserSelector selector);

    List<Friend> findFriends(Integer id);

    List<Friend> findGoOfficeFriends(Integer id);

    int add(Friend friend);

    int update(Friend friend);

    int remove(Friend friend);

}
