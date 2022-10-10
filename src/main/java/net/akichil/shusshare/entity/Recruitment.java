package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.akichil.shusshare.validation.AddGroup;
import net.akichil.shusshare.validation.SetGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Recruitment {

    private Integer recruitmentId;

    private Integer createdBy;

    @NotNull(groups = {AddGroup.class, SetGroup.class})
    private Integer shusshaId;

    @NotNull(groups = {AddGroup.class, SetGroup.class})
    @Size(min = 1, max = 30, groups = {AddGroup.class, SetGroup.class})
    private String title;

    @NotNull(groups = {AddGroup.class, SetGroup.class})
    private RecruitmentGenre genre;

    private LocalDateTime deadline;

    private Integer capacity;

    private Integer participantCount;

    private RecruitmentStatus status;

    private Integer lockVersion;

}
