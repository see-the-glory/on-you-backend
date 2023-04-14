package stg.onyou.model.network.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Builder
@Data
public class CommentCreateRequest {

    @NotEmpty
    private String content;
    private Long parentId;
    private List<Long> mentionUserList;
}
