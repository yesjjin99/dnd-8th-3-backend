package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentDto;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentResponse;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardComment;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBoardCommentServiceImpl implements QuestionBoardCommentService {
    private final QuestionBoardRepository questionBoardRepository;
    private final QuestionBoardCommentRepository questionBoardCommentRepository;
    private final UserRepository userRepository;
    private final ProfileService profileService;

    @Override
    public QuestionBoardCommentResponse createComment(User user, Long questionBoardArticleId, QuestionBoardCommentDto commentDto) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });
        User findUser = userRepository.findById(user.getId()).orElseThrow(()->{
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        Profile profile = findUser.getProfile();

        QuestionBoardComment questionBoardComment = QuestionBoardComment.builder()
                .questionBoard(questionBoard)
                .author(profile)
                .body(commentDto.getBody())
                .build();

        QuestionBoardComment comment = questionBoardCommentRepository.save(questionBoardComment);

        return convertComment(comment);
    }

    private QuestionBoardCommentResponse convertComment(QuestionBoardComment questionBoardComment) {

        ProfileResponse profile = profileService.getProfile(questionBoardComment.getAuthor().getNickName());

        return QuestionBoardCommentResponse.builder()
                .id(questionBoardComment.getId())
                .author(QuestionBoardCommentResponse.Author.builder()
                        .nickname(profile.getNickname())
                        .profilePath(profile.getImage()).build())
                .body(questionBoardComment.getBody())
                .createdAt(questionBoardComment.getCreatedDate())
                .updatedAt(questionBoardComment.getModifiedDate())
                .build();
    }
}
