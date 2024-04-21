package msyaipulanwar.restful;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.model.RegisterUserRequest;
import msyaipulanwar.restful.model.UpdateUserRequest;
import msyaipulanwar.restful.model.UserResponse;
import msyaipulanwar.restful.model.WebResponse;
import msyaipulanwar.restful.repository.ContactRepository;
import msyaipulanwar.restful.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

     @Autowired
     private UserRepository userRepository;

     @Autowired
     private ContactRepository contactRepository;

     @BeforeEach
     void setUp() {
         contactRepository.deleteAll();
         userRepository.deleteAll();

     }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("test");
        request.setName("test");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk())
                .andDo(result -> {
                    WebResponse<String> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    assertEquals("Ok", stringWebResponse.getData());
                });
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest())
                .andDo(result -> {
                    WebResponse<String> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });
                    log.warn(String.valueOf(stringWebResponse));
                    assertNotNull(stringWebResponse.getErrors());
                });
    }

    @Test
    void testRegisterDuplicate() throws Exception {
         User user = new User();
         user.setName("test");
         user.setPassword("test");
         user.setUsername("test");
         userRepository.save(user);

         RegisterUserRequest request = new RegisterUserRequest();
         request.setUsername("test");
         request.setPassword("test");
         request.setName("test");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest())
                .andDo(result -> {
                    WebResponse<String> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNotNull(stringWebResponse.getErrors());
                });
    }

    @Test
    void testGetUserFailed() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setUsername("test");
        user.setToken("IniUniqueToken");
        user.setTokenExpired(System.currentTimeMillis()*1000*2);
        userRepository.save(user);

        mockMvc.perform(
                        get("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "TEST"))
                .andExpectAll(
                        status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<UserResponse> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNotNull(stringWebResponse.getErrors());
                });
    }

    @Test
    void testGetUserTokenExpired() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setUsername("test");
        user.setToken("TEST");
        user.setTokenExpired(System.currentTimeMillis());
        userRepository.save(user);

        mockMvc.perform(
                        get("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "TEST"))
                .andExpectAll(
                        status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<UserResponse> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNotNull(stringWebResponse.getErrors());
                });
    }

    @Test
    void testGetUserSuccess() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setUsername("test");
        user.setToken("TEST");
        user.setTokenExpired(System.currentTimeMillis()*1000*5);
        userRepository.save(user);

        mockMvc.perform(
                        get("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "TEST"))
                .andExpectAll(
                        status().isOk())
                .andDo(result -> {
                    WebResponse<UserResponse> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNull(stringWebResponse.getErrors());
                    assertNotNull(stringWebResponse.getData());
                });
    }

    @Test
    void testUpdateUserFailed() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setUsername("test");
        user.setToken("IniUniqueToken");
        user.setTokenExpired(System.currentTimeMillis()*1000*2);
        userRepository.save(user);

        User request = new User();
        request.setName("test");

        mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .header("X-API-TOKEN", "TEST")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<UserResponse> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNotNull(stringWebResponse.getErrors());
                });
    }

    @Test
    void testUpdateUserNoRequest() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setUsername("test");
        user.setToken("TEST");
        user.setTokenExpired(System.currentTimeMillis()*1000*2);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .header("X-API-TOKEN", "TEST")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk())
                .andDo(result -> {
                    WebResponse<UserResponse> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNull(stringWebResponse.getErrors());
                });
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPassword("test");
        user.setUsername("test");
        user.setToken("TEST");
        user.setTokenExpired(System.currentTimeMillis()*1000*2);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("test5");

        mockMvc.perform(
                        patch("/api/users/current")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .header("X-API-TOKEN", "TEST")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk())
                .andDo(result -> {
                    WebResponse<UserResponse> stringWebResponse = objectMapper
                            .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                    log.warn(String.valueOf(stringWebResponse));
                    assertNull(stringWebResponse.getErrors());
                });
    }

}
