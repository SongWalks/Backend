package com.sookmyung.swapclass.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(400, "잘못된 입력값입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    NOT_FOUND(404, "존재하지 않는 리소스입니다."),
    CONFLICT(409, "이미 존재하는 리소스입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류입니다."),

    // 회원
    EMAIL_DUPLICATED(409, "이미 사용 중인 이메일입니다."),
    EMAIL_NOT_VERIFIED(403, "이메일 인증이 완료되지 않았습니다."),
    INVALID_PASSWORD(400, "비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    USER_SUSPENDED(403, "정지된 계정입니다."),

    // 게시글
    POST_NOT_FOUND(404, "존재하지 않는 게시글입니다."),
    POST_NOT_MODIFIABLE(400, "수정할 수 없는 게시글입니다."),
    WANTED_COURSE_DUPLICATED(400, "원하는 과목이 중복되었습니다."),
    DISCARD_COURSE_CANNOT_BE_WANTED(400, "버릴 과목은 원하는 과목으로 등록할 수 없습니다."),

    // 강의
    COURSE_NOT_FOUND(404, "존재하지 않는 강의입니다."),

    // 교환 요청
    PROPOSAL_ALREADY_EXISTS(409, "이미 교환 요청을 보낸 게시글입니다."),
    PROPOSAL_EXPIRED(400, "만료된 교환 요청입니다."),
    PROPOSAL_NOT_FOUND(404, "존재하지 않는 교환 요청입니다."),

    // 교환
    EXCHANGE_NOT_FOUND(404, "존재하지 않는 교환입니다."),
    EXCHANGE_ALREADY_COMPLETED(400, "이미 완료된 교환입니다."),

    // 신고
    REPORT_IMAGE_REQUIRED(400, "신고 사진을 첨부해주세요.");

    private final int status;
    private final String message;
}