package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.entity.Club;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubScheduleResponse {

    private Long id;
    private String name;
    private String location;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
