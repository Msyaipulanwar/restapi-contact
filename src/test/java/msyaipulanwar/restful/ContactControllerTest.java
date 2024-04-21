package msyaipulanwar.restful;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msyaipulanwar.restful.entity.Contact;
import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.model.ContactResponse;
import msyaipulanwar.restful.model.CreateContactRequest;
import msyaipulanwar.restful.model.UpdateContactRequest;
import msyaipulanwar.restful.model.WebResponse;
import msyaipulanwar.restful.repository.ContactRepository;
import msyaipulanwar.restful.repository.UserRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {
    private static final Logger log = LoggerFactory.getLogger(ContactControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
        user.setName("admin");
        user.setToken("TEST");
        user.setTokenExpired(System.currentTimeMillis() * 1000 * 60);
        userRepository.save(user);
    }

    @Test
    void createContactsUnauthorized() throws Exception {
        CreateContactRequest createContactRequest = new CreateContactRequest();
        createContactRequest.setFirstName("test");
        createContactRequest.setEmail("test");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createContactRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createContactsBadRequest() throws Exception {
        CreateContactRequest createContactRequest = new CreateContactRequest();
        createContactRequest.setFirstName("test");
        createContactRequest.setPhone("+628968625w427");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
                        .content(mapper.writeValueAsString(createContactRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.warn(String.valueOf(response));
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createContactsSuccess() throws Exception {
        CreateContactRequest createContactRequest = new CreateContactRequest();
        createContactRequest.setFirstName("test");
        createContactRequest.setEmail("test@gmail.com");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
                        .content(mapper.writeValueAsString(createContactRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
        });
    }

    // ---------------------------------------- GET CONTACT

    @Test
    void getContactNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/798778789")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.warn(String.valueOf(response));
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getContactSucces() throws Exception {
        User user = userRepository.findById("admin").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("test");
        contact.setEmail("test@gmail.com");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.warn(String.valueOf(response));
            assertNull(response.getErrors());
            assertNotNull(response.getData());
        });
    }

    //----------------------------------------- Update Contact
    @Test
    void updateContactsUnauthorized() throws Exception {
        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        updateContactRequest.setFirstName("after update");
        updateContactRequest.setEmail("after update");

        mockMvc.perform(
                put("/api/contacts/3444")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateContactsBadRequest() throws Exception {
        User user = userRepository.findById("admin").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("test");
        contact.setEmail("test@gmail.com");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        updateContactRequest.setFirstName("after update");
        updateContactRequest.setEmail("after update");

        mockMvc.perform(
                put("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
                        .content(mapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.warn(String.valueOf(response));
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateContactsNotFound() throws Exception {
        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        updateContactRequest.setFirstName("after update");
        updateContactRequest.setEmail("after@gmail.com");

        mockMvc.perform(
                put("/api/contacts/222")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
                        .content(mapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.warn(String.valueOf(response));
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateContactsSuccess() throws Exception {
        User user = userRepository.findById("admin").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("test");
        contact.setEmail("test@gmail.com");
        contact.setUser(user);
        contactRepository.save(contact);

        UpdateContactRequest updateContactRequest = new UpdateContactRequest();
        updateContactRequest.setFirstName("after update");
        updateContactRequest.setEmail("update@gmail.com");

        mockMvc.perform(
                put("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
                        .content(mapper.writeValueAsString(updateContactRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
        });
    }

    @Test
    void deleteContactsNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/222")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.warn(String.valueOf(response));
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteContactsSuccess() throws Exception {
        User user = userRepository.findById("admin").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("test");
        contact.setEmail("test@gmail.com");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/"+contact.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "TEST")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
        });
    }

}
