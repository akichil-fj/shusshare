package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RecruitmentGenre {

    NONE(0),
    LUNCH(1),
    CAFE(2);

    private final int value;

    public static RecruitmentGenre getRecruitmentGenre(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
