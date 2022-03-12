package stg.onyou.exception;

import lombok.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExceptionResponse {
    private final LocalDateTime transactionTime = LocalDateTime.now();
    private final String error;
    private final String message;

    public static ResponseEntity<ExceptionResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .error(errorCode.getHttpStatus().name())
                        .message(errorCode.getDetail())
                        .build()
                );
    }
}