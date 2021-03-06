package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.FriendDetail;
import net.akichil.shusshare.entity.ShusshaFriends;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import net.akichil.shusshare.repository.mybatis.FriendMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class FriendRepositoryImpl implements FriendRepository {

    private final SqlSession sqlSession;

    public FriendRepositoryImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<FriendDetail> findAllUser(UserSelector selector) {
        return sqlSession.getMapper(FriendMapper.class).findAllUser(selector);
    }

    @Override
    public List<FriendDetail> findFriendFromUser(Integer id) {
        return sqlSession.getMapper(FriendMapper.class).findFriendsFromUser(id);
    }

    @Override
    public List<FriendDetail> findFriendsToUser(Integer id) {
        return sqlSession.getMapper(FriendMapper.class).findFriendsToUser(id);
    }

    @Override
    public List<ShusshaFriends> findGoOfficeFriend(Integer id, LocalDate startDate, LocalDate endDate) {
        return sqlSession.getMapper(FriendMapper.class).findGoOfficeFriends(id, startDate, endDate);
    }

    @Override
    public FriendDetail findFriendByAccountId(Integer accountId, Integer accountIdFrom) {
        FriendDetail friend = sqlSession.getMapper(FriendMapper.class).findFriendByAccountId(accountId, accountIdFrom);
        if (friend == null) {
            throw new ResourceNotFoundException();
        }
        return friend;
    }

    @Override
    public FriendDetail findFriendByAccountId(String userId, Integer accountIdFrom) {
        FriendDetail friend = sqlSession.getMapper(FriendMapper.class).findFriendByUserId(userId, accountIdFrom);
        if (friend == null) {
            throw new ResourceNotFoundException();
        }
        return friend;
    }

    @Override
    public void add(Friend friend) {
        sqlSession.getMapper(FriendMapper.class).insert(friend);
    }

    @Override
    public void set(Friend friend) {
        final int affected = sqlSession.getMapper(FriendMapper.class).update(friend);
        if (affected != 1) {
            throw new ResourceNotFoundException("");
        }
    }

    @Override
    public void remove(Friend friend) {
        final int affected = sqlSession.getMapper(FriendMapper.class).delete(friend);
        if (affected != 1) {
            throw new ResourceNotFoundException();
        }
    }
}
