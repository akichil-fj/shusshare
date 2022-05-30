package net.akichil.shusshare.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendList {

    private List<User> acceptedFriends;

    private List<User> requestedFriends;

    private List<User> pendingFriends;

}
