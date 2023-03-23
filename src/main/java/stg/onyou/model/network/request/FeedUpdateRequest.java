package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.AccessModifier;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedUpdateRequest {

    @NotNull
    @Size(max = 100, message = "글자 수를 100자 이내로 입력해주세요.")
    String content;
    @NotNull
    AccessModifier access; // PUBLIC, PRIVATE
    Long clubId;
}
