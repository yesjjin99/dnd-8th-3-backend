package d83t.bpmbackend.domain.aggregate.profile.controller;

import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileUpdateRequest;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileUpdateResponse;
import d83t.bpmbackend.domain.aggregate.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/api/profile")
    public ProfileUpdateResponse updateProfile(@ModelAttribute ProfileUpdateRequest profileUpdateRequest,
                                               @RequestParam MultipartFile file){
        log.info("request: {}", profileUpdateRequest.toString());
        return profileService.updateProfile(profileUpdateRequest, file);
    }
}
