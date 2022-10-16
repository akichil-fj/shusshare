package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.akichil.shusshare.validation.AddGroup;
import net.akichil.shusshare.validation.SetGroup;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecruitmentForEdit extends Recruitment {

    @Pattern(regexp = "^\\d{1,2}:\\d{1,2}$", groups = {AddGroup.class, SetGroup.class}, message = "{net.akichil.validation.constraints.Pattern.deadline.message}")
    private String deadlineStr;

    @Digits(integer = 5, fraction = 0, groups = {AddGroup.class, SetGroup.class}, message = "{net.akichil.validation.constraints.Pattern.capacity.message}")
    private String capacityStr;

    public void set(Recruitment recruitment) {
        setRecruitmentId(recruitment.getRecruitmentId());
        setCreatedBy(recruitment.getCreatedBy());
        setShusshaId(recruitment.getShusshaId());
        setTitle(recruitment.getTitle());
        setDescription(recruitment.getDescription());
        setGenre(recruitment.getGenre());
        setCapacity(recruitment.getCapacity());
        setParticipantCount(recruitment.getParticipantCount());
        setDeadline(recruitment.getDeadline());
        setDate(recruitment.getDate());
        setStatus(recruitment.getStatus());
        setLockVersion(recruitment.getLockVersion());
        if (recruitment.getDeadline() != null) {
            setDeadlineStr(recruitment.getDeadline().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (recruitment.getCapacity() != null) {
            setCapacityStr(recruitment.getCapacity().toString());
        }
    }

}
