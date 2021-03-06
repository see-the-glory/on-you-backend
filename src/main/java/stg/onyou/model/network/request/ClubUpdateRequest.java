package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubUpdateRequest {

    private Long category1Id;
    private Long category2Id;
    @Size(max = 20, message = "클럽 명을 20자 이내로 입력해주세요.")
    private String clubName;
    @Pattern(regexp = "^[Y|N]{1}$", message ="Y 또는 N 값만 가능합니다.")
    private String isApproveRequired;
    @PositiveOrZero
    @Range(min = 0, max = 100, message = "클럽 정원은 100명 이내로 해주세요")
    private Integer clubMaxMember;
    @Size(max = 20, message = "20자 이내로 간단하게 입력해주세요.")
    private String clubShortDesc;
    private String clubLongDesc;
    private String thumbnailUrl;
}
