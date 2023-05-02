package stg.onyou.model.network.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class GuestCommentCreateRequest {

    @NotEmpty
    @Size(max = 100, message = "방명록은 100자 제")
    private String content;
}