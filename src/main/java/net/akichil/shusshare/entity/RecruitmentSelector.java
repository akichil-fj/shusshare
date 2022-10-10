package net.akichil.shusshare.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RecruitmentSelector {

    private Integer accountId;

    private Integer createdById;

    private Integer shusshaId;

    private LocalDate startDate;

    private LocalDate endDate;

}
