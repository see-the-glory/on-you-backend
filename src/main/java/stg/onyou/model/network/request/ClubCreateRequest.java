package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateRequest {

    @NotNull
    @Positive
    private Long category1Id;
    @Positive
    private Long category2Id;
    @NotEmpty
    @Size(max = 8, message = "클럽 명을 8자 이내로 입력해주세요.")
    private String clubName;

    @Pattern(regexp = "^[Y|N]{1}$", message ="Y 또는 N 값만 가능합니다.")
    private String isApproveRequired;

    @NotNull
    @PositiveOrZero
    @Range(min = 0, max = 100, message = "클럽 정원은 100명 이내로 해주세요.")
    private Integer clubMaxMember;

    @NotEmpty
    @Size(max = 20, message = "클럽의 간단한 소개를 입력해주세요.")
    private String clubShortDesc;
    @NotEmpty
    @Size(max = 1000, message = "소개글은 1000자 이내여야 합니다.")
    private String clubLongDesc;

    private String thumbnailUrl;

}
