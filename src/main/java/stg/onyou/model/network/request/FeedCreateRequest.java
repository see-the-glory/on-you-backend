package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedCreateRequest {

    private Long id;
    private Long clubId;
    @NotNull
    @Size(max = 1000, message = "글자 수를 1000자 이내로 입력해주세요.")
    private String content;
    private List<Long> mentionUserList;
}
