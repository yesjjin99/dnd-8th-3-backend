package d83t.bpmbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum Error {

    INVALID_REQUEST("Invalid request value. Please check your request body.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_USER("Unauthorized user. Please check your token.", HttpStatus.UNAUTHORIZED),
    FILE_TRANSFER_FAIL("Failed to transfer file", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND_USER_ID("user id not found", HttpStatus.NOT_FOUND),
    NOT_FOUND_KAKAO_ID("kakao user not found", HttpStatus.NOT_FOUND),
    NOT_FOUND_PROFILE("user profile not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXITS("kakaoId already exits", HttpStatus.CONFLICT),
    USER_ALREADY_REGISTER_SCHEDULE("user already register schedule", HttpStatus.BAD_REQUEST),
    USER_NICKNAME_ALREADY_EXITS("user nickname already exits", HttpStatus.CONFLICT),
    S3_UPLOAD_FAIL("upload fail", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_GET_FILE_FAIL("fail to get file", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_USER_NICK_NAME("duplicate user nickname", HttpStatus.CONFLICT),
    NOT_FOUND_BODY_SHAPE("bodyshape article not found", HttpStatus.NOT_FOUND),
    FILE_SIZE_MAX("a Maximum of 5 files can Come in", HttpStatus.BAD_REQUEST),
    NOT_FOUND_LOCATION("location not found", HttpStatus.NOT_FOUND),
    NOT_FOUND_STUDIO("studio not found", HttpStatus.NOT_FOUND),
    NOT_FOUND_SCHEDULE("schedule not found", HttpStatus.NO_CONTENT),
    STUDIO_ALREADY_EXISTS("studio already exists", HttpStatus.CONFLICT),
    NOT_FOUND_REVIEW("review not found", HttpStatus.NOT_FOUND),
    NOT_FOUND_LIKE("like not found", HttpStatus.NOT_FOUND),
    NOT_FOUND_QUESTION_ARTICLE("question article not found", HttpStatus.NOT_FOUND),
    ALREADY_FAVORITE_QUESTION_BOARD("This is a post on the question board that I have already liked.", HttpStatus.UNPROCESSABLE_ENTITY),
    ALREADY_UN_FAVORITE_QUESTION_BOARD("This is a post on the question board that I have already disliked.", HttpStatus.UNPROCESSABLE_ENTITY),
    NOT_MATCH_USER("the author and the logged-in user are different", HttpStatus.CONFLICT),
    FILE_REQUIRED("file is required", HttpStatus.NOT_FOUND),
    NOT_FOUND_STORY("story not found", HttpStatus.NOT_FOUND),
    NOT_AUTHOR_OF_POST("not author of post", HttpStatus.FORBIDDEN),
    NOT_FOUND_QUESTION_BOARD_COMMENT("not found question board article comment", HttpStatus.NOT_FOUND),
    NOT_FOUND_QUESTION_BOARD_OR_COMMENT("not found question board or comment", HttpStatus.NOT_FOUND),
    NOT_FOUND_QUESTION_BOARD_COMMENT_PARENT_ID("not found question board comment parent id", HttpStatus.NOT_FOUND),
    DIFF_POST_CHILD_ID_PARENT_ID("The parent comment and the child comment have different post numbers.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_SCRAP("scrap not found", HttpStatus.NOT_FOUND);



    private final String message;
    private final HttpStatus status;

    Error(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

}
