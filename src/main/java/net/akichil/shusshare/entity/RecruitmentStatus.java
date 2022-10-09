package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RecruitmentStatus {

    NONE(0),
    OPENED(1),
    CLOSED(2),
    CANCELED(3),
    DELETED(4);

    private final int value;

    public static RecruitmentStatus getRecruitmentStatus(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
