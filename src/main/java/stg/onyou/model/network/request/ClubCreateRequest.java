package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateRequest {

    private String categoryId;
    private String clubName;
    private int clubMaxMember;
    private String clubDescription;
    private int creatorId;

}
