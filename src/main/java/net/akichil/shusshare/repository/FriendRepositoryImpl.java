package net.akichil.shusshare.repository;

import net.akichil.shusshare.entity.Friend;
import net.akichil.shusshare.entity.UserSelector;
import net.akichil.shusshare.repository.mybatis.FriendMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendRepositoryImpl implements FriendRepository {

    private final SqlSession sqlSession;

    public FriendRepositoryImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<Friend> findAllUser(UserSelector selector) {
        return sqlSession.getMapper(FriendMapper.class).findAllUser(selector);
    }

    @Override
    public List<Friend> findFriendFromUser(Integer id) {
        return sqlSession.getMapper(FriendMapper.class).findFriendsFromUser(id);
    }

    @Override
    public List<Friend> findFriendsToUser(Integer id) {
        return sqlSession.getMapper(FriendMapper.class).findFriendsToUser(id);
    }

    @Override
    public List<Friend> findGoOfficeFriend(Integer id) {
        return sqlSession.getMapper(FriendMapper.class).findGoOfficeFriends(id);
    }

    @Override
    public Friend findOne(Integer id) {
        return null;
    }

    @Override
    public void add(Friend friend) {

    }

    @Override
    public void set(Friend friend) {

    }

    @Override
    public void remove(Friend friend) {

    }
}
