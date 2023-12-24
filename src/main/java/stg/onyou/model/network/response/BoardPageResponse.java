package stg.onyou.model.network.response;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPageResponse {

    private Boolean hasNext;
    private Boolean hasData;
    private Page<BoardResponse> responses;

}
