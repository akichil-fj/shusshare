package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.akichil.shusshare.validation.CompareEquals;
import net.akichil.shusshare.validation.SetPasswordGroup;

/**
 * ハッシュ化前のパスワードを入れる
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@CompareEquals(value1 = "newPassword", value2 = "confirmPassword", groups={SetPasswordGroup.class}, message = "{net.akichil.validation.constraints.CompareEquals.message}")
public class EditPassword {

    private String oldPassword;

    @Size(min = 4, groups = {SetPasswordGroup.class})
    private String newPassword;

    private String confirmPassword;

}
