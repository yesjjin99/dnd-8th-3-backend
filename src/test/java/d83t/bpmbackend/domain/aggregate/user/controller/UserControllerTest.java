package d83t.bpmbackend.domain.aggregate.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import d83t.bpmbackend.Utils.DateUtils;
import d83t.bpmbackend.config.WithAuthUser;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileRequest;
import d83t.bpmbackend.domain.aggregate.profile.dto.ProfileResponse;
import d83t.bpmbackend.domain.aggregate.user.dto.ScheduleRequest;
import d83t.bpmbackend.domain.aggregate.user.dto.ScheduleResponse;
import d83t.bpmbackend.domain.aggregate.user.dto.UserRequestDto;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.service.UserServiceImpl;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import d83t.bpmbackend.security.jwt.JwtService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    JwtService jwtService;

    private final Random random = new Random();
    private ProfileRequest profileRequest;
    private ProfileResponse profileResponse;
    private UserRequestDto userRequestDto;
    private ScheduleRequest scheduleRequest;
    private ScheduleResponse scheduleResponse;

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
                .scheduleName("스케줄 이름")
                .date("2022-01-01")
                .time("17:54:32")
                .memo("메모입니다.")
                .studioName("스튜디오 이롬")
                .build();

        scheduleResponse = ScheduleResponse.builder()
                .studioName(scheduleRequest.getStudioName())
                .time(DateUtils.convertTimeFormat(scheduleRequest.getTime()))
                .date(DateUtils.convertDateFormat(scheduleRequest.getDate()))
                .memo(scheduleRequest.getMemo())
                .build();
    }

    @Test
    @DisplayName("키카오 로그인")
    void testSignUp() throws Exception {
        byte[] imageBytes = {0x04, 0x02, 0x03, 0x04};
        MockMultipartFile image = new MockMultipartFile("file", "test.png", "image/png", imageBytes);

        Mockito.when(userService.signUp(Mockito.any(ProfileRequest.class), Mockito.any(MultipartFile.class))).thenReturn(profileResponse);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users/signup")
                        .file(image)
                        .param("nickname", profileRequest.getNickname())
                        .param("bio", profileRequest.getBio())
                        .param("kakaoId", String.valueOf(profileRequest.getKakaoId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname", Matchers.equalTo(profileResponse.getNickname())));
    }


    @Test
    @DisplayName("카카오 ID 검증")
    void testVerification() throws Exception {
        Random random = new Random();
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .kakaoId(random.nextLong())
                .build();

        Mockito.when(userService.verification(Mockito.any(userRequestDto.getClass()))).thenReturn(profileResponse);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname", Matchers.equalTo(profileResponse.getNickname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bio", Matchers.equalTo(profileResponse.getBio())));
    }

    @Test
    @DisplayName("카카오 ID 검증 실패")
    void testVerificationFail() throws Exception {
        Mockito.when(userService.verification(Mockito.any(userRequestDto.getClass()))).thenThrow(new CustomException(Error.NOT_FOUND_USER_ID));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo(Error.NOT_FOUND_USER_ID.getMessage())));
    }

    @Test
    @WithAuthUser
    @DisplayName("일정 조회")
    void testScheduleGet() throws Exception {

        Mockito.when(userService.getSchedule(Mockito.any(User.class))).thenReturn(scheduleResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/schedule"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithAuthUser
    @DisplayName("일정 삭제")
    void testScheduleDelete() throws Exception{

        Mockito.doNothing().when(userService).deleteSchedule(Mockito.any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/schedule"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithAuthUser
    @DisplayName("일정 등록")
    void testSchedulePost() throws Exception {

        Mockito.when(userService.registerSchedule(Mockito.any(User.class), Mockito.any(ScheduleRequest.class))).thenReturn(scheduleResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleRequest))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}