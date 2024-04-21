package msyaipulanwar.restful;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.model.TokenResponse;
import msyaipulanwar.restful.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import msyaipulanwar.restful.model.LoginUserRequest;
import msyaipulanwar.restful.model.WebResponse;
import msyaipulanwar.restful.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);
    @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void loginFailedUserNotFound() throws Exception {
    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc.perform(
        post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(
            status().isUnauthorized())
        .andDo(
            result -> {
              WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                  new TypeReference<>() {
                  });
              log.warn(String.valueOf(response));
              assertNotNull(response.getErrors());
            });
  }

  @Test
  void loginFailedWrongPass() throws Exception {
      User user = new User();
      user.setName("test");
      user.setUsername("test");
      user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
      userRepository.save(user);

      LoginUserRequest request = new LoginUserRequest();
      request.setUsername("test");
      request.setPassword("test1");

      mockMvc.perform(
                    post("/api/auth/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpectAll(
                    status().isUnauthorized())
            .andDo(
                    result -> {
                      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                              new TypeReference<>() {
                              });
                      assertNotNull(response.getErrors());
                    });
  }

    @Test
    void loginSuccess() throws Exception {
        User user = new User();
        user.setName("test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
                        post("/api/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk())
                .andDo(
                        result -> {
                            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            log.warn(String.valueOf(response));
                            assertNull(response.getErrors());
                            assertNotNull(response.getData());
                            assertNotNull(response.getData().getToken());
                            assertNotNull(response.getData().getExpireAt());

                            User userDb = userRepository.findById("test").orElse(null);
                            assertNotNull(userDb);
                            assertEquals(userDb.getToken(), response.getData().getToken());
                            assertEquals(userDb.getTokenExpired(), response.getData().getExpireAt());
                        });
    }

    @Test
    void logoutUserFailed() throws Exception {
        mockMvc.perform(
                        delete("/api/auth/logout")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isUnauthorized())
                .andDo(
                        result -> {
                            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            log.warn(String.valueOf(response));
                            assertNotNull(response.getErrors());
                        });
    }

    @Test
    void logoutUserSuccess() throws Exception {
      User user = new User();
      user.setName("test");
      user.setUsername("test");
      user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
      user.setToken("TEST");
      user.setTokenExpired(System.currentTimeMillis()*1000*60);
      userRepository.save(user);

        mockMvc.perform(
                        delete("/api/auth/logout")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "TEST"))
                .andExpectAll(
                        status().isOk())
                .andDo(
                        result -> {
                            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            log.warn(String.valueOf(response));
                            assertNull(response.getErrors());
                        });
    }
}