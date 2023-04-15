package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaInfoRequest {
//    @NotEmpty
    private String currentVersion;
    private String deviceInfo;
}
