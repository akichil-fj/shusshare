package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.UserSelector;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FriendMapper {

    List<FriendDetail> findAllUser(UserSelector selector);

    List<FriendDetail> findFriendsFromUser(Integer id);

    List<FriendDetail> findFriendsToUser(Integer id);

    List<FriendDetail> findGoOfficeFriends(@Param("accountId") Integer accountId,
                                           @Param("date") LocalDate date);

    FriendDetail findFriendByAccountId(@Param(value = "accountId") Integer accountId,
                                       @Param(value = "accountIdFrom") Integer accountIdFrom);

    void insert(Friend friend);

    int update(Friend friend);

    int delete(Friend friend);

}
