package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recruitment {

    private Integer recruitmentId;

    private Integer createdBy;

    private Integer shusshaId;

    private String title;

    private RecruitmentGenre genre;

    private LocalDateTime deadline;

    private Integer participantCount;

    private RecruitmentStatus status;

    private Integer lockVersion;

}
