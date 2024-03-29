package net.akichil.shusshare.service;

import net.akichil.shusshare.entity.*;
import net.akichil.shusshare.repository.AccountRepository;
import net.akichil.shusshare.repository.FriendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    private final AccountRepository accountRepository;

    public FriendServiceImpl(FriendRepository friendRepository, AccountRepository accountRepository) {
        this.friendRepository = friendRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @Override
    public List<FriendDetail> findAllUser(UserSelector selector) {
        return friendRepository.findAllUser(selector);
    }

    @Transactional
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

    @Transactional
    @Override
    public List<ShusshaFriends> findGoOfficeFriend(Integer id) {
        LocalDate today = LocalDate.now();
        return friendRepository.findGoOfficeFriend(id, today, null);
    }

    @Transactional
    @Override
    public FriendDetail findFriendByAccountId(Integer accountId, Integer accountIdFrom) {
        return friendRepository.findFriendByAccountId(accountId, accountIdFrom);
    }

    @Transactional
    @Override
    public FriendDetail findFriendByUserId(String userId, Integer accountIdFrom) {
        return friendRepository.findFriendByAccountId(userId, accountIdFrom);
    }

    @Transactional
    @Override
    public void add(Friend friend) {
        friendRepository.add(friend);
    }

    @Transactional
    @Override
    public void set(Friend friend) {
        friendRepository.set(friend);
    }

    @Transactional
    @Override
    public void remove(Integer accountId, Integer accountIdFrom) {
        Friend friend = friendRepository.findFriendByAccountId(accountId, accountIdFrom);
        friendRepository.remove(friend);
    }

    @Transactional
    @Override
    public void request(Integer accountId, Integer accountIdFrom) {
        Friend friend = new Friend();
        friend.setAccountIdTo(accountId);
        friend.setAccountIdFrom(accountIdFrom);

        // Accountが非公開ならREQUESTED、それ以外はFOLLOWED
        Account account = accountRepository.findOne(accountId);
        if (account.getStatus() == AccountStatus.PRIVATE) {
            friend.setStatus(FriendStatus.REQUESTED);
        } else {
            friend.setStatus(FriendStatus.FOLLOWED);
        }

        add(friend);
    }

    @Transactional
    @Override
    public void allow(Integer accountIdFrom, Integer accountIdTo) {
        FriendDetail friend = friendRepository.findFriendByAccountId(accountIdTo, accountIdFrom);
        friend.setStatus(FriendStatus.FOLLOWED);

        set(friend);
    }

    @Transactional
    @Override
    public void deny(Integer accountIdFrom, Integer accountIdTo) {
        FriendDetail friend = friendRepository.findFriendByAccountId(accountIdTo, accountIdFrom);

        friend.setStatus(FriendStatus.REJECTED);
        set(friend);
    }

}
