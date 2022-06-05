package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FriendStatus {

    NONE(0),
    FOLLOWED(1),
    REQUESTED(2),
    REJECTED(3);

    private final int value;

    public static FriendStatus getFriendStatus(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
