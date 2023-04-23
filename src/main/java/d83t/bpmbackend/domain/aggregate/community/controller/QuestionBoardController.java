package d83t.bpmbackend.domain.aggregate.community.controller;

import d83t.bpmbackend.domain.aggregate.community.dto.*;
import d83t.bpmbackend.domain.aggregate.community.service.QuestionBoardService;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/question-board")
@Slf4j
public class QuestionBoardController {
    private final QuestionBoardService questionBoardService;

    @Operation(summary = "질문하기 게시판 게시글 등록 API", description = "사용자가 질문하기 게시판에 질문을 등록합니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 등록 성공", content = @Content(schema = @Schema(implementation = QuestionBoardResponse.SingleQuestionBoard.class)))
    @ApiResponse(responseCode = "400", description = "이미지가 5개 넘게 들어왔습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "잘못된 유저가 들어왔습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public QuestionBoardResponse.SingleQuestionBoard createQuestionBoard(
            @AuthenticationPrincipal User user,
            @Nullable @RequestPart List<MultipartFile> files,
            @ModelAttribute QuestionBoardRequest questionBoardRequest) {
        log.info("Data input : {}", questionBoardRequest.toString());
        return QuestionBoardResponse.SingleQuestionBoard.builder().questionBoardResponse(questionBoardService.createQuestionBoardArticle(user, files, questionBoardRequest)).build();
    }

    @Operation(summary = "질문하기 게시판 리스트 조회 API", description = "사용자가 질문하기 게시판 리스트 조회합니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 리스트 조회 성공", content = @Content(schema = @Schema(implementation = QuestionBoardResponse.MultiQuestionBoard.class)))
    @GetMapping
    public QuestionBoardResponse.MultiQuestionBoard getQuestionBoardArticles(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            @ModelAttribute QuestionBoardParam questionBoardParam) {
        List<QuestionBoardResponse> questionArticles = questionBoardService.getQuestionBoardArticles(user, limit, offset, questionBoardParam);
        return QuestionBoardResponse.MultiQuestionBoard.builder().questionBoardResponseList(questionArticles).questionBoardCount(questionArticles.size()).build();
    }

    @Operation(summary = "질문하기 게시판 상세 조회 API", description = "사용자가 질문하기 게시판 중 하나의 게시글을 클릭해서 상세 조회합니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 상세조회 성공", content = @Content(schema = @Schema(implementation = QuestionBoardResponse.SingleQuestionBoard.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{questionBoardArticleId}")
    public QuestionBoardResponse.SingleQuestionBoard getQuestionBoardArticle(
            @AuthenticationPrincipal User user,
            @PathVariable Long questionBoardArticleId) {
        return QuestionBoardResponse.SingleQuestionBoard.builder().questionBoardResponse(questionBoardService.getQuestionBoardArticle(user, questionBoardArticleId)).build();
    }


    @Operation(summary = "질문하기 게시판 게시글 수정 API", description = "사용자가 질문하기 게시판 중 하나의 게시글을 클릭해서  수정합니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 상세조회 성공", content = @Content(schema = @Schema(implementation = QuestionBoardResponse.SingleQuestionBoard.class)))
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{questionBoardArticleId}")
    public QuestionBoardResponse.SingleQuestionBoard updateQuestionBoardArticle(
            @AuthenticationPrincipal User user,
            @Nullable @RequestPart List<MultipartFile> files,
            @Nullable @ModelAttribute QuestionBoardRequest questionBoardRequest,
            @PathVariable Long questionBoardArticleId) {
        log.info("data input: {}", questionBoardRequest.toString());
        return QuestionBoardResponse.SingleQuestionBoard.builder().questionBoardResponse(questionBoardService.updateQuestionBoardArticle(user, files, questionBoardRequest, questionBoardArticleId)).build();
    }

    @Operation(summary = "질문하기 게시판 게시글 삭제 API", description = "사용자가 질문하기 게시판 중 하나의 게시글을 클릭해서 삭제합니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 게시글 삭제 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{questionBoardArticleId}")
    public void deleteQuestionBoardArticle(
            @AuthenticationPrincipal User user,
            @PathVariable Long questionBoardArticleId) {
        log.info("question board delete input : {}", questionBoardArticleId);
        questionBoardService.deleteQuestionBoardArticle(user, questionBoardArticleId);

    }

    @Operation(summary = "질문하기 게시판 게시글 좋아요 API", description = "사용자가 질문하기 게시판 중 하나의 게시글을 클릭해서 좋아요를 누릅니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 게시글 좋아요 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{questionBoardArticleId}/favorite")
    public QuestionBoardResponse.SingleQuestionBoard favoriteQuestionBoardArticle(
            @AuthenticationPrincipal User user,
            @PathVariable Long questionBoardArticleId) {
        log.info("question board favorite input : {}", questionBoardArticleId);
        return QuestionBoardResponse.SingleQuestionBoard.builder().questionBoardResponse(questionBoardService.favoriteQuestionBoardArticle(user, questionBoardArticleId)).build();
    }

    @Operation(summary = "질문하기 게시판 게시글 좋아요 취소 API", description = "사용자가 질문하기 게시판 중 하나의 게시글을 클릭해서 좋아요를 취소합니다. token을 넘겨야합니다.")
    @ApiResponse(responseCode = "200", description = "질문하기 게시판 게시글 좋아요 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{questionBoardArticleId}/unfavorite")
    public QuestionBoardResponse.SingleQuestionBoard unfavoriteQuestionBoardArticle(
            @AuthenticationPrincipal User user,
            @PathVariable Long questionBoardArticleId) {
        log.info("question board favorite input : {}", questionBoardArticleId);
        return QuestionBoardResponse.SingleQuestionBoard.builder().questionBoardResponse(questionBoardService.unfavoriteQuestionBoardArticle(user, questionBoardArticleId)).build();
    }

}
