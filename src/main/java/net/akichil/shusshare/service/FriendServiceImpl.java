package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.FriendRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    public FriendServiceImpl(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @Override
    public List<FriendDetail> findAllUser(UserSelector selector) {
        return friendRepository.findAllUser(selector);
    }

    @Override
    public FriendList findFriends(Integer id) {
        FriendList friendList = new FriendList();

        List<FriendDetail> friendFromUser = friendRepository.findFriendFromUser(id);
        List<FriendDetail> friendsToUser = friendRepository.findFriendsToUser(id);

        friendList.setFollowing(friendFromUser.stream()
                .filter(f -> f.getStatus() == FriendStatus.FOLLOWED)
                .collect(Collectors.toList()));

        friendList.setRequesting(friendFromUser.stream()
                .filter(f -> f.getStatus() == FriendStatus.REQUESTED)
                .collect(Collectors.toList()));

        friendList.setFollowers(friendsToUser.stream()
                .filter(f -> f.getStatus() == FriendStatus.FOLLOWED)
                .collect(Collectors.toList()));

        friendList.setRequested(friendsToUser.stream()
                .filter(f -> f.getStatus() == FriendStatus.REQUESTED)
                .collect(Collectors.toList()));

        return friendList;
    }

    @Override
    public List<FriendDetail> findGoOfficeFriend(Integer id) {
        LocalDate today = LocalDate.now();
        return friendRepository.findGoOfficeFriend(id, today);
    }

    @Override
    public FriendDetail findFriendByAccountId(Integer accountId, Integer accountIdFrom) {
        return friendRepository.findFriendByAccountId(accountId, accountIdFrom);
    }

    @Override
    public void add(Friend friend) {
        friendRepository.add(friend);
    }

    @Override
    public void set(Friend friend) {
        friendRepository.set(friend);
    }

    @Override
    public void remove(Integer accountId, Integer accountIdFrom) {
        Friend friend = friendRepository.findFriendByAccountId(accountId, accountIdFrom);
        friendRepository.remove(friend);
    }

}
