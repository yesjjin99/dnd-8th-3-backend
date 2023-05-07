package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardParam;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardRequest;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardResponse;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardFavorite;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardImage;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardFavoriteRepository;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardRepository;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileResponse;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.repository.ProfileRepository;
import d83t.bpmbackend.domain.aggregate.profile.service.ProfileService;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import d83t.bpmbackend.s3.S3UploaderService;
import d83t.bpmbackend.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBoardServiceImpl implements QuestionBoardService {

    private final UserRepository userRepository;
    private final S3UploaderService uploaderService;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final QuestionBoardFavoriteRepository questionBoardFavoriteRepository;

    @Value("${bpm.s3.bucket.question.board.path}")
    private String questionBoardPath;

    @Value("${bpm.s3.bucket.base}")
    private String basePath;

    @Value("${spring.environment}")
    private String env;

    private String fileDir;

    @PostConstruct
    private void init() {
        if (env.equals("local")) {
            this.fileDir = FileUtils.getUploadPath();
        } else if (env.equals("prod")) {
            this.fileDir = this.basePath + this.questionBoardPath;
        }
    }

    @Override
    public QuestionBoardResponse createQuestionBoardArticle(User user, List<MultipartFile> files, QuestionBoardRequest questionBoardRequest) {
        //file은 최대 5개만 들어올 수 있다.
        if (files != null && files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        Profile profile = findUser.getProfile();
        QuestionBoard questionBoard = QuestionBoard.builder()
                .author(profile)
                .slug(questionBoardRequest.getTitle())
                .content(questionBoardRequest.getContent())
                .build();

        List<String> filePaths = new ArrayList<>();
        if (files != null && files.size() != 0) {
            for (MultipartFile file : files) {
                String newName = FileUtils.createNewFileName(file.getOriginalFilename());
                String filePath = fileDir + newName;
                questionBoard.addQuestionBoardImage(QuestionBoardImage.builder()
                        .originFileName(newName)
                        .storagePathName(filePath)
                        .questionBoard(questionBoard)
                        .build());
                filePaths.add(filePath);
                if (env.equals("prod")) {
                    uploaderService.putS3(file, questionBoardPath, newName);
                } else if (env.equals("local")) {
                    try {
                        File localFile = new File(filePath);
                        file.transferTo(localFile);
                        FileUtils.removeNewFile(localFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        questionBoardRepository.save(questionBoard);

        return convertResponse(user, questionBoard);
    }

    @Transactional(readOnly = true)
    @Override
    public List<QuestionBoardResponse> getQuestionBoardArticles(User user, Integer limit, Integer offset, QuestionBoardParam questionBoardParam) {
        List<QuestionBoard> questionBoards = new ArrayList<>();

        limit = limit == null ? 20 : limit;
        offset = offset == null ? 0 : offset;
        Pageable pageable = PageRequest.of(offset, limit);

        if (questionBoardParam.getNickname() != null) {
            Profile profile = profileRepository.findByNickName(questionBoardParam.getNickname()).orElseThrow(() -> {
                throw new CustomException(Error.NOT_FOUND_PROFILE);
            });
            Long profileId = profile.getId();
            questionBoards = questionBoardRepository.findByProfileId(pageable, profileId);

        } else if (questionBoardParam.getSlug() != null) {
            questionBoards = questionBoardRepository.searchQuestionBoardNames(questionBoardParam.getSlug(), pageable);
        } else {
            questionBoards = questionBoardRepository.findByAll(pageable);
        }
        return questionBoards.stream().map(questionBoard -> {
            return convertResponse(user, questionBoard);
        }).collect(Collectors.toList());

    }

    @Override
    public QuestionBoardResponse getQuestionBoardArticle(User user, Long questionBoardArticleId) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });

        List<String> filePaths = new ArrayList<>();
        List<QuestionBoardImage> images = questionBoard.getImage();
        for (QuestionBoardImage image : images) {
            filePaths.add(image.getStoragePathName());
        }
        return convertResponse(user, questionBoard);
    }

    @Override
    public QuestionBoardResponse updateQuestionBoardArticle(User user, List<MultipartFile> files, QuestionBoardRequest questionBoardRequest, Long questionBoardArticleId) {
        if (files != null && files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });

        if (questionBoardRequest.getTitle() != null) {
            questionBoard.changeTitle(questionBoardRequest.getTitle());
        }
        if (questionBoardRequest.getContent() != null) {
            questionBoard.changeContent(questionBoardRequest.getContent());
        }
        List<QuestionBoardImage> boardImages = questionBoard.getImage();
        List<String> filePaths = boardImages.stream()
                .map(QuestionBoardImage::getStoragePathName)
                .collect(Collectors.toList());

        //파일 수정
        if (files != null && files.size() != 0) {
            filePaths = new ArrayList<String>();
            List<QuestionBoardImage> questionBoardImages = new ArrayList<>();
            for (MultipartFile file : files) {
                String newName = FileUtils.createNewFileName(file.getOriginalFilename());
                String filePath = fileDir + newName;

                questionBoardImages.add(QuestionBoardImage.builder()
                        .originFileName(newName)
                        .storagePathName(filePath)
                        .questionBoard(questionBoard)
                        .build());
                filePaths.add(filePath);
                if (env.equals("prod")) {
                    uploaderService.putS3(file, questionBoardPath, newName);
                } else if (env.equals("local")) {
                    try {
                        File localFile = new File(filePath);
                        file.transferTo(localFile);
                        FileUtils.removeNewFile(localFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            questionBoard.changeImage(questionBoardImages);
        }
        questionBoardRepository.save(questionBoard);

        return convertResponse(user, questionBoard);
    }

    public void deleteQuestionBoardArticle(User user, Long questionBoardArticleId) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });

        //작성자가 맞는지
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        Long userId = findUser.getId();
        log.info("db userId : {}", userId);
        log.info("author : {}", questionBoard.getAuthor().getId());
        if (questionBoard.getAuthor().getId().equals(userId)) {
            questionBoardRepository.delete(questionBoard);
        } else {
            throw new CustomException(Error.NOT_MATCH_USER);
        }
    }

    @Override
    public QuestionBoardResponse favoriteQuestionBoardArticle(User user, Long questionBoardArticleId) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        questionBoardFavoriteRepository.findByQuestionBoardIdAndUserId(questionBoard.getId(), user.getId()).ifPresent(e -> {
            throw new CustomException(Error.ALREADY_FAVORITE_QUESTION_BOARD);
        });


        QuestionBoardFavorite favorite = QuestionBoardFavorite.builder().questionBoard(questionBoard).user(findUser).build();

        questionBoardFavoriteRepository.save(favorite);

        return convertResponse(user, questionBoard);
    }

    @Override
    public QuestionBoardResponse unfavoriteQuestionBoardArticle(User user, Long questionBoardArticleId) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });
        userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        QuestionBoardFavorite favorite = questionBoardFavoriteRepository.findByQuestionBoardIdAndUserId(questionBoard.getId(), user.getId()).orElseThrow(() -> {
            throw new CustomException(Error.ALREADY_UN_FAVORITE_QUESTION_BOARD);
        });

        questionBoardFavoriteRepository.delete(favorite);
        return convertResponse(user, questionBoard);
    }

    private QuestionBoardResponse convertResponse(User user, QuestionBoard questionBoard) {
        ProfileResponse profile = profileService.getProfile(questionBoard.getAuthor().getNickName());
        List<String> imagePaths = List.of();
        if (questionBoard.getImage() != null) {
            imagePaths = questionBoard.getImage().stream()
                    .map(QuestionBoardImage::getStoragePathName)
                    .collect(Collectors.toList());
        }
        return QuestionBoardResponse.builder()
                .id(questionBoard.getId())
                .author(QuestionBoardResponse.Author.builder()
                        .nickname(profile.getNickname())
                        .profilePath(profile.getImage()).build())
                .createdAt(questionBoard.getCreatedDate())
                .updatedAt(questionBoard.getModifiedDate())
                .content(questionBoard.getContent())
                .slug(questionBoard.getSlug())
                .favorited(getFavoritesStatus(user, questionBoard))
                .favoritesCount(getFavoritesCount(questionBoard.getId()))
                .commentsCount(questionBoard.getComments().size())
                .filesPath(imagePaths)
                .build();
    }

    private Boolean getFavoritesStatus(User user, QuestionBoard questionBoard) {
        if (user == null) return false;
        Optional<QuestionBoardFavorite> favoriteStatus = questionBoardFavoriteRepository.findByQuestionBoardIdAndUserId(questionBoard.getId(), user.getId());
        return favoriteStatus.isEmpty() ? false : true;
    }

    private Long getFavoritesCount(Long questionBoardId) {
        return questionBoardFavoriteRepository.countByQuestionBoardId(questionBoardId);
    }
}
