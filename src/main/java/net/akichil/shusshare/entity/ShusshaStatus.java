package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ShusshaStatus {

    NONE(0),
    TOBE(1),
    DONE(2),
    CANCEL(3);

    private final int value;

    public static ShusshaStatus getShusshaStatus(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
