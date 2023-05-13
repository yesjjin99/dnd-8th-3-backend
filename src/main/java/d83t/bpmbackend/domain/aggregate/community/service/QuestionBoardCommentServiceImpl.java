package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentDto;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentReportDto;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentResponse;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentUpdateDto;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardComment;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardCommentReport;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardCommentQueryDSLRepository;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardCommentReportRepository;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardCommentRepository;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardRepository;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileResponse;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.service.ProfileService;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBoardCommentServiceImpl implements QuestionBoardCommentService {
    private final QuestionBoardRepository questionBoardRepository;
    private final QuestionBoardCommentRepository questionBoardCommentRepository;
    private final QuestionBoardCommentQueryDSLRepository questionBoardCommentQueryDSLRepository;
    private final QuestionBoardCommentReportRepository questionBoardCommentReportRepository;
    private final UserRepository userRepository;
    private final ProfileService profileService;

    @Override
    public QuestionBoardCommentResponse createComment(User user, Long questionBoardArticleId, QuestionBoardCommentDto commentDto) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });
        QuestionBoardComment parent = null;
        //자식 댓글인 경우
        if (commentDto.getParentId() != null) {
            parent = questionBoardCommentRepository.findById(commentDto.getParentId()).orElseThrow(() -> {
                throw new CustomException(Error.NOT_FOUND_QUESTION_BOARD_COMMENT_PARENT_ID);
            });
            if (parent.getQuestionBoard().getId() != questionBoardArticleId) {
                throw new CustomException(Error.DIFF_POST_CHILD_ID_PARENT_ID);
            }
        }

        Profile profile = findUser.getProfile();

        QuestionBoardComment questionBoardComment = QuestionBoardComment.builder()
                .questionBoard(questionBoard)
                .author(profile)
                .body(commentDto.getBody())
                .build();

        if (parent != null) {
            questionBoardComment.updateParent(parent);
        }

        QuestionBoardComment comment = questionBoardCommentRepository.save(questionBoardComment);

        return convertComment(comment);
    }

    @Override
    public List<QuestionBoardCommentResponse> getComments(User user, Long questionBoardArticleId) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });
        List<QuestionBoardComment> comments = questionBoardCommentQueryDSLRepository.findAllByQuestionComment(questionBoard).stream()
                .filter(questionBoardComment -> questionBoardComment.getQuestionBoard().getId().equals(questionBoardArticleId)
                ).collect(Collectors.toList());

        List<QuestionBoardCommentResponse> result = new ArrayList<>();
        Map<Long, QuestionBoardCommentResponse> map = new HashMap<>();

        comments.stream().forEach(c -> {
            QuestionBoardCommentResponse cdto = convertComment(c);
            map.put(c.getId(), cdto);
            if (c.getParent() != null) map.get(c.getParent().getId()).addChildren(cdto);
            else result.add(cdto);
        });
        return result;
    }

    @Override
    public QuestionBoardCommentResponse updateComment(User user, Long questionBoardArticleId, Long commentId, QuestionBoardCommentUpdateDto questionBoardCommentUpdateDto) {
        QuestionBoardComment questionBoardComment = questionBoardCommentRepository.findByQuestionBoardIdAndId(questionBoardArticleId, commentId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_BOARD_OR_COMMENT);
        });

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        Profile profile = findUser.getProfile();
        //작성자인지 확인
        if (!questionBoardComment.getAuthor().getId().equals(profile.getId())) {
            throw new CustomException(Error.NOT_AUTHOR_OF_POST);
        }

        questionBoardComment.updateBody(questionBoardCommentUpdateDto.getBody());

        questionBoardCommentRepository.save(questionBoardComment);

        return convertComment(questionBoardComment);
    }

    @Override
    public void deleteComment(User user, Long questionBoardArticleId, Long commentId) {
        QuestionBoardComment questionBoardComment = questionBoardCommentRepository.findByQuestionBoardIdAndId(questionBoardArticleId, commentId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_BOARD_OR_COMMENT);
        });

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        Profile profile = findUser.getProfile();
        //작성자인지 확인
        if (!questionBoardComment.getAuthor().getId().equals(profile.getId())) {
            throw new CustomException(Error.NOT_AUTHOR_OF_POST);
        }
        questionBoardCommentRepository.delete(questionBoardComment);
    }

    @Override
    public void reportComment(User user, Long questionBoardArticleId, Long commentId, QuestionBoardCommentReportDto reportDto) {
        QuestionBoardComment questionBoardComment = questionBoardCommentRepository.findByQuestionBoardIdAndId(questionBoardArticleId, commentId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_BOARD_OR_COMMENT);
        });

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        //신고 3회 삭제.
        if(questionBoardComment.getReportCount() >= 2){
            questionBoardCommentRepository.delete(questionBoardComment);
        }else{
            questionBoardComment.plusReport();
            questionBoardCommentRepository.save(questionBoardComment);
        }

        //로그성 테이블에 남기기
        QuestionBoardCommentReport questionBoardCommentReport = QuestionBoardCommentReport.builder()
                .commentAuthor(questionBoardComment.getAuthor().getNickName())
                .commentBody(questionBoardComment.getBody())
                .commentId(questionBoardComment.getId())
                .commentCreatedAt(questionBoardComment.getCreatedDate())
                .commentUpdatedAt(questionBoardComment.getModifiedDate())
                .reportReason(reportDto.getReason())
                .reporter(findUser.getProfile().getId())
                .build();

        questionBoardCommentReportRepository.save(questionBoardCommentReport);

    }

    private QuestionBoardCommentResponse convertComment(QuestionBoardComment questionBoardComment) {

        ProfileResponse profile = profileService.getProfile(questionBoardComment.getAuthor().getNickName());
        if (questionBoardComment.getParent() != null) {
            return QuestionBoardCommentResponse.builder()
                    .id(questionBoardComment.getId())
                    .author(QuestionBoardCommentResponse.Author.builder()
                            .nickname(profile.getNickname())
                            .profilePath(profile.getImage()).build())
                    .body(questionBoardComment.getBody())
                    .reportCount(questionBoardComment.getReportCount())
                    .parentId(questionBoardComment.getParent().getId())
                    .createdAt(questionBoardComment.getCreatedDate())
                    .updatedAt(questionBoardComment.getModifiedDate())
                    .build();
        } else {
            return QuestionBoardCommentResponse.builder()
                    .id(questionBoardComment.getId())
                    .author(QuestionBoardCommentResponse.Author.builder()
                            .nickname(profile.getNickname())
                            .profilePath(profile.getImage()).build())
                    .body(questionBoardComment.getBody())
                    .reportCount(questionBoardComment.getReportCount())
                    .createdAt(questionBoardComment.getCreatedDate())
                    .updatedAt(questionBoardComment.getModifiedDate())
                    .build();
        }
    }
}
