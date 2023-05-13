package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.StoryRequestDto;
import d83t.bpmbackend.domain.aggregate.community.dto.StoryResponseDto;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoryService {

    StoryResponseDto createStory(StoryRequestDto storyRequestDto, List<MultipartFile> files, User user);
    List<StoryResponseDto> getAllStory(int page, int size, String sort, User user);
    StoryResponseDto getStory(Long storyId, User user);
    StoryResponseDto updateStory(Long storyId, StoryRequestDto storyRequestDto, List<MultipartFile> files, User user);
    void deleteStory(Long storyId, User user);

}
