package d83t.bpmbackend.domain.aggregate.user.service;

import d83t.bpmbackend.Utils.DateUtils;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileDto;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileRequest;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileResponse;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.repository.ProfileRepository;
import d83t.bpmbackend.domain.aggregate.profile.service.ProfileImageService;
import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import d83t.bpmbackend.domain.aggregate.studio.repository.StudioRepository;
import d83t.bpmbackend.domain.aggregate.user.dto.ScheduleRequest;
import d83t.bpmbackend.domain.aggregate.user.dto.ScheduleResponse;
import d83t.bpmbackend.domain.aggregate.user.dto.UserRequestDto;
import d83t.bpmbackend.domain.aggregate.user.entity.Schedule;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.ScheduleRepository;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import d83t.bpmbackend.security.jwt.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ProfileImageService imageService;

    @Mock
    private StudioRepository studioRepository;

    @Mock
    private JwtService jwtService;

    private final Random random = new Random();
    private ProfileRequest profileRequest;
    private ProfileResponse profileResponse;
    private UserRequestDto userRequestDto;
    private ScheduleRequest scheduleRequest;
    private ScheduleResponse scheduleResponse;
    private MultipartFile image;

    @BeforeEach
    void init() {

        profileRequest = ProfileRequest.builder()
                .kakaoId(random.nextLong())
                .bio("bio")
                .nickname("nickname")
                .build();

        profileResponse = ProfileResponse.builder()
                .nickname(profileRequest.getNickname())
                .bio(profileRequest.getBio())
                .image("https://images")
                .token("token")
                .build();

        userRequestDto = UserRequestDto.builder()
                .kakaoId(random.nextLong())
                .build();

        scheduleRequest = ScheduleRequest.builder()
                .date("2022-01-01")
                .time("17:54:32")
                .memo("메모입니다.")
                .studioName("스튜디오 이름")
                .build();

        scheduleResponse = ScheduleResponse.builder()
                .studioName(scheduleRequest.getStudioName())
                .time(DateUtils.convertTimeFormat(scheduleRequest.getTime()))
                .date(DateUtils.convertDateFormat(scheduleRequest.getDate()))
                .memo(scheduleRequest.getMemo())
                .build();

        byte[] imageBytes = {0x04, 0x02, 0x03, 0x04};
        image = new MockMultipartFile("file", "test.png", "image/png", imageBytes);
    }

    @Test
    @DisplayName("카카오 프로필 등록 실패 - 카카오ID 이미 존재")
    void testSignUpFailByKakaoID() {

        Mockito.when(userRepository.findByKakaoId(Mockito.eq(profileRequest.getKakaoId()))).thenReturn(
                Optional.ofNullable(User.builder()
                        .kakaoId(profileRequest.getKakaoId())
                        .build())
        );
        CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
            userService.signUp(profileRequest, image);
        });
        Assertions.assertEquals(exception.getError(), Error.USER_ALREADY_EXITS);
    }

    @Test
    @DisplayName("카카오 프로필 등록 실패 - 닉네임 중복")
    void testSignUpFailByDuplicateProfileNickname() {

        Mockito.when(userRepository.findByKakaoId(Mockito.eq(profileRequest.getKakaoId()))).thenReturn(Optional.empty());
        Mockito.when(profileRepository.findByNickName(Mockito.eq(profileRequest.getNickname()))).thenReturn(
                Optional.ofNullable(Profile.builder()
                        .build())
        );

        CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
            userService.signUp(profileRequest, image);
        });
        Assertions.assertEquals(exception.getError(), Error.USER_NICKNAME_ALREADY_EXITS);
    }

    @Test
    @DisplayName("카카오 프로필 등록")
    void testSignUpSuccess() {
        ProfileDto profileDto = ProfileDto
                .builder()
                .nickname(profileRequest.getNickname())
                .bio(profileRequest.getBio())
                .imageName(image.getName())
                .imagePath("https://")
                .build();

        Mockito.when(userRepository.findByKakaoId(Mockito.eq(profileRequest.getKakaoId()))).thenReturn(Optional.empty());
        Mockito.when(profileRepository.findByNickName(Mockito.eq(profileRequest.getNickname()))).thenReturn(Optional.empty());
        Mockito.when(imageService.createProfileDto(Mockito.any(ProfileRequest.class), Mockito.eq(image))).thenReturn(profileDto);
        Mockito.when(jwtService.createToken(Mockito.eq(profileRequest.getNickname()))).thenReturn("token");

        ProfileResponse result = userService.signUp(profileRequest, image);

        Assertions.assertEquals(result.getBio(), profileDto.getBio());
        Assertions.assertEquals(result.getNickname(), profileDto.getNickname());
        Assertions.assertEquals(result.getImage(), profileDto.getImagePath());
        Assertions.assertEquals(result.getToken(), "token");
    }

    @Test
    @DisplayName("카카오 ID 검증 실패")
    void testVerificationFail() {

        Mockito.when(userRepository.findByKakaoId(Mockito.eq(userRequestDto.getKakaoId()))).thenReturn(Optional.empty());

        CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
            userService.verification(userRequestDto);
        });

        Assertions.assertEquals(exception.getError(), Error.NOT_FOUND_USER_ID);
    }

    @Test
    @DisplayName("카카오 ID 검증 성공")
    void testVerification() {
        Profile userProfile = Profile.builder()
                .bio("user bio")
                .nickName("user nickname")
                .storagePathName("https://")
                .build();

        Mockito.when(userRepository.findByKakaoId(Mockito.eq(userRequestDto.getKakaoId()))).thenReturn(
                Optional.ofNullable(User.builder()
                        .profile(userProfile)
                        .build())
        );

        ProfileResponse result = userService.verification(userRequestDto);
        Assertions.assertEquals(result.getNickname(), userProfile.getNickName());
        Assertions.assertEquals(result.getBio(), userProfile.getBio());
        Assertions.assertEquals(result.getImage(), userProfile.getStoragePathName());
    }

    @Test
    @DisplayName("스케줄 등록 실패 - 이미 스케줄을 등록")
    void testSchedulePostFailAlreadyExits() {
        User user = User.builder()
                .id(123L)
                .build();

        Mockito.when(studioRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.of(Studio.builder()
                        .name("스튜디오 이름")
                        .build()));
        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(
                Optional.ofNullable(Schedule.builder().build())
        );

        CustomException exception = Assertions.assertThrows(CustomException.class, () ->{
            userService.registerSchedule(user, scheduleRequest);
        });

        Assertions.assertEquals(exception.getError(), Error.USER_ALREADY_REGISTER_SCHEDULE);
    }

    @Test
    @DisplayName("스케줄 등록 - 스튜디오 이름 있을 때")
    void testSchedulePost(){
        User user = User.builder()
                .id(123L)
                .build();

        Mockito.when(studioRepository.findByName(Mockito.any(String.class))).thenReturn(
                Optional.of(Studio.builder()
                        .name("스튜디오 이름이지롱")
                        .build()));
        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(Optional.empty());

        ScheduleResponse result = userService.registerSchedule(user, scheduleRequest);
        Assertions.assertEquals(result.getStudioName(), "스튜디오 이름이지롱");
        Assertions.assertEquals(result.getDate(), DateUtils.convertDateFormat(scheduleRequest.getDate()));
        Assertions.assertEquals(result.getTime(), DateUtils.convertTimeFormat(scheduleRequest.getTime()));
        Assertions.assertEquals(result.getMemo(), scheduleRequest.getMemo());
    }

    @Test
    @DisplayName("스케줄 등록 - 스튜디오 이름 없을 때")
    void testSchedulePostStudioNameIsNull(){
        User user = User.builder()
                .id(123L)
                .build();

        Mockito.when(studioRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.empty());
        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(Optional.empty());

        ScheduleResponse result = userService.registerSchedule(user, scheduleRequest);
        Assertions.assertEquals(result.getStudioName(),scheduleRequest.getStudioName());
        Assertions.assertEquals(result.getDate(), DateUtils.convertDateFormat(scheduleRequest.getDate()));
        Assertions.assertEquals(result.getTime(), DateUtils.convertTimeFormat(scheduleRequest.getTime()));
        Assertions.assertEquals(result.getMemo(), scheduleRequest.getMemo());
    }

    @Test
    @DisplayName("스케줄 조회 실패")
    void testScheduleGetFail() {
        User user = User.builder()
                .id(99877L)
                .build();
        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(Optional.empty());

        CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
            userService.getSchedule(user);
        });

        Assertions.assertEquals(exception.getError(), Error.NOT_FOUND_SCHEDULE);
    }

    @Test
    @DisplayName("스케줄 조회 - 스튜디오 이름 없을 때")
    void testScheduleGetStudioNameIsNull() {
        User user = User.builder()
                .id(99877L)
                .build();

        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(
                Optional.of(Schedule.builder()
                        .memo("메모")
                        .build()));

        ScheduleResponse schedule = userService.getSchedule(user);
        Assertions.assertEquals(schedule.getMemo(), "메모");
        Assertions.assertEquals(schedule.getStudioName(), "");
    }

    @Test
    @DisplayName("스케줄 조회 - 스튜디오 이름 있을 때")
    void testScheduleGetStudioNameIsNotNull() {
        User user = User.builder()
                .id(99877L)
                .build();

        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(
                Optional.of(Schedule.builder()
                        .studio(Studio.builder()
                                .name("스튜디오 이름")
                                .build())
                        .memo("메모")
                        .build()));

        ScheduleResponse schedule = userService.getSchedule(user);
        Assertions.assertEquals(schedule.getMemo(), "메모");
        Assertions.assertEquals(schedule.getStudioName(), "스튜디오 이름");
    }

    @Test
    @DisplayName("스케줄 삭제 실패")
    void testScheduleDeleteFail() {
        User user = User.builder()
                .id(99877L)
                .build();
        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(Optional.empty());

        CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
            userService.deleteSchedule(user);
        });

        Assertions.assertEquals(exception.getError(), Error.NOT_FOUND_SCHEDULE);
    }

    @Test
    @DisplayName("스케줄 삭제")
    void testScheduleDelete() {
        User user = User.builder()
                .id(99877L)
                .build();
        Mockito.when(scheduleRepository.findByUserId(Mockito.eq(user.getId()))).thenReturn(
                Optional.ofNullable(Schedule.builder()
                        .build())
        );

        userService.deleteSchedule(user);
    }


}