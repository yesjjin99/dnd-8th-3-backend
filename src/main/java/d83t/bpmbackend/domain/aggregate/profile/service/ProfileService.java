package d83t.bpmbackend.domain.aggregate.profile.service;

import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileResponse;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileUpdateRequest;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileUpdateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    ProfileUpdateResponse updateProfile(ProfileUpdateRequest profileUpdateRequest, MultipartFile file);

    ProfileResponse getProfile(String nickname);
}
