package stg.onyou.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {


    // 400 Bad request
    FILE_EMPTY(BAD_REQUEST, "업로드 한 파일이 존재하지 않습니다."),
    LOGIN_FAIL(BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다."),
    // 401 UNAUTHORIZED : 인증되지 않은 사용자
//    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),
//    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다."),
    NO_PERMISSION(FORBIDDEN, "권한이 없습니다"),

//    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
//    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),
    UNSUPPORTED_EXTENSION(BAD_REQUEST, "지원하지 않는 포맷입니다."),
    // 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다."),
    CLUB_MEMBER_FULL(CONFLICT, "클럽의 정원이 마감되었습니다."),


    // 404 NOT_FOUND : Resource 를 찾을 수 없음
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자입니다."),
    CLUB_NOT_FOUND(NOT_FOUND, "존재하지 않는 클럽입니다."),
    FEED_NOT_FOUND(NOT_FOUND, "존재하지 않는 FEED입니다."),
    HASHTAG_NOT_FOUND(NOT_FOUND, "존재하지 않는 해시태그입니다."),
    CATEGORY_NOT_FOUND(NOT_FOUND, "카테고리가 존재하지 않습니다."),
    USER_CLUB_NOT_FOUND(NOT_FOUND, "사용자와 모임의 관계가 없습니다."),
    USER_APPROVE_ERROR(NOT_FOUND, "가입신청 이력이 없거나 이미 가입된 사용자입니다."),
    CLUB_SCHEDULE_NOT_FOUND(NOT_FOUND, "존재하지 않는 clubSchedule 입니다."),
    USER_CLUB_SCHEDULE_NOT_FOUND(NOT_FOUND, "UserClubSchedule이 존재하지 않습니다"),
    CLUB_CATEGORY_NOT_FOUND(NOT_FOUND, "ClubCaegory가 존재하지 않습니다."),
    ORGANIZATION_NOT_FOUND(NOT_FOUND, "orgnanization이 존재하다지 않습니다"),
    COMMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 Comment입니다."),
    ACTION_NOT_FOUND(NOT_FOUND, "존재하지 않는 Action입니다 ."),

    // 503 : Internal server error
    CLUB_CREATION_ERROR(INTERNAL_SERVER_ERROR, "클럽 생성에 실패하였습니다"),
    CLUB_REGISTER_ERROR(INTERNAL_SERVER_ERROR, "클럽 등록에 실패하였습니다"),
    CLUB_SCHEDULE_MUTATION_ERROR(INTERNAL_SERVER_ERROR, "클럽일정 생성에 실패하였습니다"),
    FEED_UPDATE_ERROR(INTERNAL_SERVER_ERROR, "FEED 업데이트에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}