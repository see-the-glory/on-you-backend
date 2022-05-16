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
    @Size(max = 20, message = "클럽 명을 20자 이내로 입력해주세요.")
    private String clubName;

    @NotNull
    @PositiveOrZero
    @Range(min = 0, max = 100, message = "클럽 정원은 100명 이내로 해주세요")
    private Integer clubMaxMember;

    @NotEmpty
    @Size(max = 20, message = "클럽의 간단한 소개를 입력해주세")
    private String clubShortDesc;
    private String clubLongDesc;

}
