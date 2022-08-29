package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubMessagesResponse {

    private Long id;
    private String action_content;
    private LocalDateTime created;

}
