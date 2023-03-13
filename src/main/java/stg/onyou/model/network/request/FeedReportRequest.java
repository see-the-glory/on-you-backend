package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ReportReason;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedReportRequest {

    @NotNull
    private ReportReason reason;
}
