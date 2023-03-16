package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.springframework.data.domain.Page;
import stg.onyou.model.RecruitStatus;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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