package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FriendStatus {

    FOLLOWED(0),
    REQUESTED(1),
    REJECTED(2),
    NONE(255);

    private final int value;

    public static FriendStatus getFriendStatus(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
