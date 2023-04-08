package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardRequest;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardResponse;
import d83t.bpmbackend.domain.aggregate.community.entity.BodyShape;
import d83t.bpmbackend.domain.aggregate.community.entity.BodyShapeImage;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBoardServiceImpl implements QuestionBoardService {

    private final UserRepository userRepository;
    private final S3UploaderService uploaderService;
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
}
