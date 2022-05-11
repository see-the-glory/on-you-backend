package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedCreateRequest {

    private Long id;
    private int club_id;
    @NotNull
    @Size(max = 100, message = "글자 수를 100자 이내로 입력해주세요.")
    private String content;
}
