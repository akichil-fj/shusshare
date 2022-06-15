package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.akichil.shusshare.validation.AddGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    private Integer accountId;

    /**
     * ユーザID
     */
    @NotNull(groups = {AddGroup.class})
    @Pattern(regexp = "^\\w{1,15}$", groups = {AddGroup.class}, message = "{net.akichil.validation.constraints.Pattern.userId.message}")
    private String userId;

    @NotNull(groups = {AddGroup.class})
    @Size(min = 1, max = 30, groups = {AddGroup.class})
    private String userName;

    @Size(min = 4, groups = {AddGroup.class})
    private String password;

    private String profilePhotoUrl;

    private AccountStatus status;

    private Integer shusshaCount;

    private Integer lockVersion;

}
