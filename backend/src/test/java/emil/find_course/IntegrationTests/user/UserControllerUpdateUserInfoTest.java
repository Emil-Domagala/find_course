package emil.find_course.IntegrationTests.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest;
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.service.FileStorageService;
import emil.find_course.user.dto.UserDto;
import emil.find_course.user.dto.request.RequestUpdateUser;
import emil.find_course.user.entity.User;
import io.micrometer.common.lang.Nullable;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerUpdateUserInfoTest extends IntegrationTestBase {
        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Value("${cookie.auth.refreshToken.name}")
        private String refreshCookieName;
        @Value("${cookie.auth.accessCookie.name}")
        private String accCookieNAme;

        @Autowired
        private JwtUtils jwtUtils;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private PrepareUserUtil prepareUserUtil;

        @Autowired
        private CookieHelperTest cookieHelper;

        @MockitoBean
        private FileStorageService fileStorageService;

        // Create setup where i will mock fileStorageService
        String dummyUrlReturned = "https://www.google.com/dummyUrl";

        @BeforeEach
        public void setUp() {
                doNothing().when(fileStorageService).deleteImage(any(String.class));
                when(fileStorageService.resizeImage(any(), anyInt(), anyInt(), anyInt(), anyLong()))
                                .thenReturn(new ByteArrayInputStream("dummy".getBytes()));
                when(fileStorageService.saveProcessedImage(any(), any(), any()))
                                .thenReturn(dummyUrlReturned);

        }

        private MockMultipartFile createUserDataPart(RequestUpdateUser requestUpdateUser) throws Exception {
                String json = objectMapper.writeValueAsString(requestUpdateUser);
                return new MockMultipartFile("userData", null, MediaType.APPLICATION_JSON_VALUE, json.getBytes());
        }

        private void assertAuthAndRefreshCookies(MvcResult result) {
                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(3);

                Map<String, CookieAttributes> cookies = setCookies.stream()
                                .map(cookieHelper::parseSetCookie)
                                .collect(Collectors.toMap(CookieAttributes::getName, ca -> ca));

                // Assert auth token cookie
                cookieHelper.testCookies(cookies.get(authCookieName), "/");

                // Assert refresh token cookie
                cookieHelper.testCookies(cookies.get(refreshCookieName),
                                "/api/v1/public/refresh-token");

                cookieHelper.testCookies(cookies.get(accCookieNAme), "/");
        }

        private RequestUpdateUser createRequestUpdateUser(@Nullable String username,
                        @Nullable String userLastname, @Nullable Boolean deleteImage, @Nullable String password) {
                if (deleteImage != null) {
                        return RequestUpdateUser.builder().username(username)
                                        .userLastname(userLastname)
                                        .password(password).deleteImage(deleteImage).build();
                }
                return RequestUpdateUser.builder().username(username)
                                .userLastname(userLastname)
                                .password(password).build();

        }

        // Test all cases when no new image was provided
        @ParameterizedTest(name = "Valid input => username: {0}, userLastname: {1}, password: {2},deleteImage: {3}")
        @DisplayName("Should update user info when no new image was provided")
        @CsvSource({
                        "JohnNewName, DoeNewLastname,  PasswordNew,",
                        "JohnNewName, DoeNewLastname,  PasswordNew, true",
                        "JohnNewName, DoeNewLastname,  PasswordNew, false",
                        "JohnNewName, DoeNewLastname,  ,",
                        "JohnNewName, DoeNewLastname,  ,true",
                        "JohnNewName, DoeNewLastname,  ,false"
        })
        public void userController_updateUserInfo_sucessfullyUpdateUserInfo(String username, String userLastname,
                        String password, Boolean deleteImage) throws Exception {
                User user = prepareUserUtil.prepareVerifiedUserWithImage();

                RequestUpdateUser requestUpdateUser = createRequestUpdateUser(username, userLastname, deleteImage,
                                password);

                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);

                String authToken = jwtUtils.generateToken(user);

                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/v1/user")
                                                .file(userDataPart)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
                UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

                assertThat(userDto.getUsername()).isEqualTo(username);
                assertThat(userDto.getUserLastname()).isEqualTo(userLastname);

                if (Boolean.TRUE.equals(deleteImage)) {
                        assertThat(userDto.getImageUrl()).isNull();
                } else {
                        assertThat(userDto.getImageUrl()).isNotNull();
                }
                if (password != null) {
                        assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
                }

                if (user.getImageUrl() != null && Boolean.TRUE.equals(deleteImage)) {
                        verify(fileStorageService).deleteImage(user.getImageUrl());
                        verify(fileStorageService, never()).saveProcessedImage(any(), any(), any());
                        verify(fileStorageService, never()).resizeImage(any(), anyInt(), anyInt(), anyInt(), anyLong());
                } else {
                        verify(fileStorageService, never()).deleteImage(user.getImageUrl());
                        verify(fileStorageService, never()).saveProcessedImage(any(), any(), any());
                        verify(fileStorageService, never()).resizeImage(any(), anyInt(), anyInt(), anyInt(), anyLong());

                }

                assertAuthAndRefreshCookies(result);
        }

        @ParameterizedTest(name = "Valid input => username: {0}, userLastname: {1}, password: {2}")
        @DisplayName("Shouldn't update password if invalid")
        @CsvSource({
                        "John, Doe,  P",
                        "John, Doe,  1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-Password",
        })
        public void userController_updateUserInfo_doNotUpdatePasswordIfInvalid(String username, String userLastname,
                        String password) throws Exception {
                User user = prepareUserUtil.prepareVerifiedUser();

                RequestUpdateUser requestUpdateUser = createRequestUpdateUser(username, userLastname, null, password);

                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);
                String authToken = jwtUtils.generateToken(user);

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/v1/user")
                                                .file(userDataPart)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        }

        @ParameterizedTest(name = "Valid input => username: {0}, userLastname: {1}")
        @DisplayName("Should return 400 when invalid input")
        @CsvSource({
                        "John, ",
                        "1, ",
                        "name1-2-3-4-5-6-7-8-9-10-11-12-13-14-15, ",
                        ", Doe",
                        ", 1",
                        ", lastname1-2-3-4-5-6-7-8-9-10-11-12-13-14-15",
        })
        public void userController_updateUserInfo_returns400WhenInvalidInput(String username, String userLastname)
                        throws Exception {
                User user = prepareUserUtil.prepareVerifiedUser();
                RequestUpdateUser requestUpdateUser = createRequestUpdateUser(username, userLastname,
                                null, null);

                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);
                String authToken = jwtUtils.generateToken(user);

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/v1/user")
                                                .file(userDataPart)
                                                .cookie(new Cookie(authCookieName, authToken)))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        }

        // <----- Test all cases when new image was provided ------->

        private static Stream<Arguments> provideUpdateUserData() {
                RequestUpdateUser withPassword = RequestUpdateUser.builder()
                                .username("John")
                                .userLastname("Doe")
                                .password("secret123")
                                .build();

                RequestUpdateUser withoutPassword = RequestUpdateUser.builder()
                                .username("Jane")
                                .userLastname("Smith")
                                .build();

                MockMultipartFile dummyImage = new MockMultipartFile(
                                "image", "avatar.jpg", "image/jpeg", "imagecontent".getBytes());

                return Stream.of(
                                Arguments.of(withPassword, dummyImage),
                                Arguments.of(withoutPassword, null),
                                Arguments.of(withPassword, null),
                                Arguments.of(withoutPassword, dummyImage));
        }

        @ParameterizedTest
        @MethodSource("provideUpdateUserData")
        @DisplayName("Should update user image and delete old one if new provided")
        public void userController_updateUserInfo_updateImage(RequestUpdateUser requestUpdateUser,
                        MultipartFile imageFile) throws Exception {

                User user = prepareUserUtil.prepareVerifiedUserWithImage();
                String authToken = jwtUtils.generateToken(user);

                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);

                MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/user");
                builder.file(userDataPart);
                builder.cookie(new Cookie(authCookieName, authToken));
                builder.with(request -> {
                        request.setMethod("PATCH");
                        return request;
                });

                if (imageFile != null) {
                        builder.file((MockMultipartFile) imageFile);
                }

                // Perform request
                MvcResult result = mockMvc.perform(builder)
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();
                assertAuthAndRefreshCookies(result);

                UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

                assertThat(userDto.getUsername()).isEqualTo(requestUpdateUser.getUsername());
                assertThat(userDto.getUserLastname()).isEqualTo(requestUpdateUser.getUserLastname());

                if (imageFile != null) {
                        assertThat(userDto.getImageUrl()).isEqualTo(dummyUrlReturned);
                        verify(fileStorageService).resizeImage(any(), anyInt(), anyInt(), anyInt(), anyLong());
                        verify(fileStorageService).saveProcessedImage(any(), any(), any());
                        verify(fileStorageService).deleteImage(anyString());
                } else {
                        assertThat(userDto.getImageUrl()).isEqualTo(user.getImageUrl());
                        verify(fileStorageService, never()).resizeImage(any(), anyInt(), anyInt(), anyInt(), anyLong());
                        verify(fileStorageService, never()).saveProcessedImage(any(), any(), any());
                        verify(fileStorageService, never()).deleteImage(anyString());
                }

        }

        // deleteImage is not called if user do not have prev and set new one

        @Test
        @DisplayName("Shouldn't call deleteImage if user do not have prev and set new one")
        public void userController_updateUserInfo_shouldNotCallDeleteImage() throws Exception {
                User user = prepareUserUtil.prepareVerifiedUser();
                String authToken = jwtUtils.generateToken(user);
                RequestUpdateUser requestUpdateUser = createRequestUpdateUser("John", "Doe",
                                null, null);
                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);
                MockMultipartFile dummyImage = new MockMultipartFile(
                                "image", "avatar.jpg", "image/jpeg", "imagecontent".getBytes());

                MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/user");
                builder.file(userDataPart);
                builder.file((MockMultipartFile) dummyImage);
                builder.cookie(new Cookie(authCookieName, authToken));
                builder.with(request -> {
                        request.setMethod("PATCH");
                        return request;
                });
                MvcResult result = mockMvc.perform(builder)
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();
                assertAuthAndRefreshCookies(result);

                UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
                assertThat(userDto.getImageUrl()).isEqualTo(dummyUrlReturned);

                verify(fileStorageService, never()).deleteImage(anyString());
                verify(fileStorageService).resizeImage(any(), anyInt(), anyInt(), anyInt(), anyLong());
                verify(fileStorageService).saveProcessedImage(any(), any(), any());
        }

        @Test
        @DisplayName("Should return 499 when no cookie set")
        public void userController_updateUserInfo_returns499WhenNoCookieSet() throws Exception {

                RequestUpdateUser requestUpdateUser = createRequestUpdateUser("John", "Doe",
                                null, null);

                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/v1/user")
                                                .file(userDataPart))
                                .andExpect(MockMvcResultMatchers.status().is(499)).andReturn();

        }

        @Test
        @DisplayName("Should return 498 when bad token passed")
        public void userController_updateUserInfo_returns498WhenNBadTokenPassed() throws Exception {

                RequestUpdateUser requestUpdateUser = createRequestUpdateUser("John", "Doe",
                                null, null);

                MockMultipartFile userDataPart = createUserDataPart(requestUpdateUser);

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/v1/user")
                                                .file(userDataPart)
                                                .cookie(new Cookie(authCookieName, "badToken")))
                                .andExpect(MockMvcResultMatchers.status().is(498)).andReturn();

        }
}
