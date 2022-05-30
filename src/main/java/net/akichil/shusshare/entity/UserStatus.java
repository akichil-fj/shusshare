package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserStatus {

    NORMAL(0),
    DELETED(1),
    NONE(255);

    private final int value;

    public static UserStatus getUserStatus(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
