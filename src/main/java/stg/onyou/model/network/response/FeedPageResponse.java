package stg.onyou.model.network.response;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedPageResponse {

    private Boolean hasNext;
    private Page<FeedResponse> responses;

}
