package stg.onyou.model.network.response;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubPageResponse {

    private Boolean hasData;
    private Boolean hasNext;
    private Page<ClubConditionResponse> responses;

}