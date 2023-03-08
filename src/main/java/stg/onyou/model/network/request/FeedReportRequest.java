package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ReportReason;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedReportRequest {
    private ReportReason reason;
}
