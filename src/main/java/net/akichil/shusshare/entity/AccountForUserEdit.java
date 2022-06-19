package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.akichil.shusshare.validation.AddGroup;
import net.akichil.shusshare.validation.CompareEquals;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@CompareEquals(value1 = "password", value2 = "confirmPassword", groups={AddGroup.class}, message = "{net.akichil.validation.constraints.CompareEquals.message}")
public class AccountForUserEdit extends Account {

    private String confirmPassword;

    private Boolean isPrivate;

    public void set(Account account) {
        setAccountId(account.getAccountId());
        setUserId(account.getUserId());
        setUserName(account.getUserName());
        setPassword(account.getPassword());
        setProfilePhotoUrl(account.getProfilePhotoUrl());
        setStatus(account.getStatus());
        setLockVersion(account.getLockVersion());
        setIsPrivate(account.getStatus() == AccountStatus.PRIVATE);
    }

}
