package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.BodyShapeResponse;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardRequest;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardResponse;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardImage;
import d83t.bpmbackend.domain.aggregate.community.repository.QuestionBoardRepository;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.repository.ProfileRepository;
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
    private final ProfileRepository profileRepository;
    private final QuestionBoardRepository questionBoardRepository;

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
        if (files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        Profile profile = findUser.getProfile();
        QuestionBoard questionBoard = QuestionBoard.builder()
                .author(profile)
                .title(questionBoardRequest.getTitle())
                .content(questionBoardRequest.getContent())
                .build();

        List<String> filePaths = new ArrayList<>();

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

        questionBoardRepository.save(questionBoard);

        return QuestionBoardResponse.builder()
                .id(questionBoard.getId())
                .createdAt(questionBoard.getCreatedDate())
                .author(QuestionBoardResponse.Author.builder()
                        .nickname(profile.getNickName())
                        .profilePath(profile.getStoragePathName())
                        .build())
                .updatedAt(questionBoard.getModifiedDate())
                .filesPath(filePaths)
                .title(questionBoard.getTitle())
                .content(questionBoard.getContent())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<QuestionBoardResponse> getQuestionBoardArticles(User user, Integer limit, Integer offset) {
        List<QuestionBoard> questionBoards = new ArrayList<>();

        limit = limit == null ? 20 : limit;
        offset = offset == null ? 0 : offset;
        Pageable pageable = PageRequest.of(offset, limit);
        Long profileId = user.getProfile().getId();
        Optional<Profile> findProfile = profileRepository.findById(profileId);
        Profile profile = findProfile.get();

        questionBoards = questionBoardRepository.findByNickName(pageable, profile.getNickName());

        return questionBoards.stream().map(questionBoard -> {
            return QuestionBoardResponse.builder()
                    .id(questionBoard.getId())
                    .title(questionBoard.getTitle())
                    .content(questionBoard.getContent())
                    .createdAt(questionBoard.getCreatedDate())
                    .updatedAt(questionBoard.getModifiedDate())
                    .filesPath(questionBoard.getImage().stream().map(images -> {
                        return images.getStoragePathName();
                    }).collect(Collectors.toList()))
                    .author(QuestionBoardResponse.Author.builder()
                            .nickname(questionBoard.getAuthor().getNickName())
                            .profilePath(questionBoard.getAuthor().getStoragePathName())
                            .build())
                    .build();
        }).collect(Collectors.toList());

    }

    @Override
    public QuestionBoardResponse getQuestionBoardArticle(User user, Long questionBoardArticleId) {
        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });

        List<String> filePaths = new ArrayList<>();
        Profile author = questionBoard.getAuthor();
        List<QuestionBoardImage> images = questionBoard.getImage();
        for (
                QuestionBoardImage image : images) {
            filePaths.add(image.getStoragePathName());
        }
        return QuestionBoardResponse.builder()
                .id(questionBoard.getId())
                .createdAt(questionBoard.getCreatedDate())
                .author(QuestionBoardResponse.Author.builder()
                        .nickname(author.getNickName())
                        .profilePath(author.getStoragePathName())
                        .build())
                .updatedAt(questionBoard.getModifiedDate())
                .filesPath(filePaths)
                .title(questionBoard.getTitle())
                .content(questionBoard.getContent())
                .build();
    }

    @Override
    public QuestionBoardResponse updateQuestionBoardArticle(User user, List<MultipartFile> files, QuestionBoardRequest questionBoardRequest, Long questionBoardArticleId) {
        if (files != null && files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).orElseThrow(() -> {
            throw new CustomException(Error.NOT_FOUND_USER_ID);
        });

        QuestionBoard questionBoard = questionBoardRepository.findById(questionBoardArticleId).orElseThrow(() ->{
            throw new CustomException(Error.NOT_FOUND_QUESTION_ARTICLE);
        });

        if(questionBoardRequest.getTitle() != null){
                questionBoard.changeTitle(questionBoardRequest.getTitle());
        }
        if(questionBoardRequest.getContent() != null){
            questionBoard.changeContent(questionBoardRequest.getContent());
        }
        List<QuestionBoardImage> boardImages = questionBoard.getImage();
        List<String> filePaths = boardImages.stream()
                .map(QuestionBoardImage::getStoragePathName)
                .collect(Collectors.toList());

        //파일 수정
        if(files != null && files.size() != 0){
            filePaths = new ArrayList<String>();
            List<QuestionBoardImage> questionBoardImages= new ArrayList<>();
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

        Profile profile = findUser.getProfile();

        return QuestionBoardResponse.builder()
                .id(questionBoard.getId())
                .createdAt(questionBoard.getCreatedDate())
                .author(QuestionBoardResponse.Author.builder()
                        .nickname(profile.getNickName())
                        .profilePath(profile.getStoragePathName())
                        .build())
                .updatedAt(questionBoard.getModifiedDate())
                .filesPath(filePaths)
                .title(questionBoard.getTitle())
                .content(questionBoard.getContent())
                .build();
    }
}
