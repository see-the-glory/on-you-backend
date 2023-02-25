package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionRequest {

    @NotEmpty
    @Size(max = 1000, message = "건의사항은 1000자 이내여야 합니다.")
    private String content;
}
