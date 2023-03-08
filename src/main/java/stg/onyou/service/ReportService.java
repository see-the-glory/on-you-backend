package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.ReportReason;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.Report;
import stg.onyou.model.entity.User;
import stg.onyou.repository.FeedRepository;
import stg.onyou.repository.ReportRepository;
import stg.onyou.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    @Transactional
    public String feedReport(Long userId, Long feedId, ReportReason reason) {

        Report findReport = reportRepository.findReportByFeedIdAndUserId(feedId, userId);
        if (findReport == null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

            feed.setReportCount(feed.getReportCount() + 1);

            Report report = Report.builder()
                    .feed(feed)
                    .user(user)
                    .reason(reason)
                    .build();

            reportRepository.save(report);
            return "신고가 접수되었습니다.";
        } else {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

    }


}
