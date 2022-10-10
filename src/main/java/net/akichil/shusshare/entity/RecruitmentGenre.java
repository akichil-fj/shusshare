package net.akichil.shusshare.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RecruitmentGenre {

    NONE(0, ""),
    LUNCH(1, "ランチ"),
    CAFE(2, "カフェ");

    private final int value;

    private final String name;

    public static RecruitmentGenre getRecruitmentGenre(int value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst().orElse(NONE);
    }

}
