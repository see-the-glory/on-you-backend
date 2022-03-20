package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateRequest {

    private Integer categoryId;
    private String clubName;
    private Integer clubMaxMember;
    private String clubDescription;

}
