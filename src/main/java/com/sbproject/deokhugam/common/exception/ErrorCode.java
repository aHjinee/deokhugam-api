package com.sbproject.deokhugam.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),
    INVALID_CREDENTIALS("아이디 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED_ACCESS("권한이 없습니다."),

	//Book
	BOOK_NOT_FOUND("도서를 찾을 수 없습니다."),
	BOOK_ALREADY_EXISTS("ISBN이 이미 존재합니다."),
	ISBN_EXTRACTION_FAILED("ISBN을 추출할 수 없습니다."),
	OCR_PROCESSING_FAILED("이미지 처리 중 오류가 발생했습니다."),
	NAVER_BOOK_NOT_FOUND("Naver API를 통해 ISBN으로 도서 정보를 찾을 수 없습니다."),

    // Post
    POST_NOT_FOUND("리뷰를 찾을 수 없습니다."),

    // Comment
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다."),
    COMMENT_NOT_OWNED("본인이 작성한 댓글만 수정/삭제할 수 있습니다."),

	// Review
	REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다."),
	REVIEW_NOT_OWNED("본인이 작성한 리뷰만 수정/삭제할 수 있습니다."),
	REVIEW_ALREADY_EXISTS("리뷰가 이미 존재합니다."),

    //Notification
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다"),

    // File
    FILE_SAVE_FAILED("파일 저장에 실패했습니다."),
    FILE_DELETE_FAILED("파일 삭제에 실패했습니다."),

    // Common
    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;
}
