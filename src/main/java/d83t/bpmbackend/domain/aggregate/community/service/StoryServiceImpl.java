package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.StoryRequestDto;
import d83t.bpmbackend.domain.aggregate.community.dto.StoryResponseDto;
import d83t.bpmbackend.domain.aggregate.community.entity.Story;
import d83t.bpmbackend.domain.aggregate.community.entity.StoryImage;
import d83t.bpmbackend.domain.aggregate.community.repository.StoryRepository;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.repository.ProfileRepository;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import d83t.bpmbackend.s3.S3UploaderService;
import d83t.bpmbackend.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final S3UploaderService uploaderService;

    @Value("${bpm.s3.bucket.story.path}")
    private String storyPath;

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
            this.fileDir = this.basePath + this.storyPath;
        }
    }

    @Override
    @Transactional
    public StoryResponseDto createStory(StoryRequestDto requestDto, List<MultipartFile> files, User user) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(Error.FILE_REQUIRED);
        }
        if (files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }

        List<String> filePaths = new ArrayList<>();
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Profile profile = findUser.getProfile();

        Story story = requestDto.toEntity(profile);

        for (MultipartFile file : files) {
            String newName = FileUtils.createNewFileName(file.getOriginalFilename());
            String filePath = fileDir + newName;

            story.addStoryImage(StoryImage.builder()
                    .originFileName(newName)
                    .storagePathName(filePath)
                    .story(story)
                    .build());
            filePaths.add(filePath);

            if (env.equals("prod")) {
                uploaderService.putS3(file, storyPath, newName);
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

        Story savedStory = storyRepository.save(story);

        return new StoryResponseDto(savedStory);
    }

    @Override
    public StoryResponseDto getStory(Long storyId, User user) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STORY));
        return new StoryResponseDto(story);
    }
}
