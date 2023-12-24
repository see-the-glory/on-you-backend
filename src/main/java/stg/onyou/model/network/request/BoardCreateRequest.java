package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.BoardCategory;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    @NotNull
    @Size(max = 2000, message = "글자 수를 2000자 이내로 입력해주세요.")
    private String content;
    private BoardCategory category;

}
