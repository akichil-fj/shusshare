package net.akichil.shusshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Friend {

    private Integer friendId;

    private Integer accountIdFrom;

    private Integer accountIdTo;

    private Integer accountId;

    private String userId;

    private String userName;

    private String profilePhotoUrl;

    private FriendStatus status;

}
