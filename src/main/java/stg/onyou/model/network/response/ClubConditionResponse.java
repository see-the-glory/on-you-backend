package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ClubConditionResponse {

    private Long id;
    private String name;
    private String clubShortDesc;
    private String clubLongDesc;

    @QueryProjection
    public ClubConditionResponse(Long id, String name, String clubLongDesc, String clubShortDesc){
        this.id = id;
        this.name = name;
        this.clubShortDesc = clubShortDesc;
        this.clubLongDesc = clubLongDesc;
    }

}
