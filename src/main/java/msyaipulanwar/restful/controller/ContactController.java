package msyaipulanwar.restful.controller;

import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.model.ContactResponse;
import msyaipulanwar.restful.model.CreateContactRequest;
import msyaipulanwar.restful.model.UpdateContactRequest;
import msyaipulanwar.restful.model.WebResponse;
import msyaipulanwar.restful.service.ContactService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }


    @PostMapping(
            path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create (User user, @RequestBody CreateContactRequest request) {
        ContactResponse contactResponse = contactService.create(user, request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get (User user, @PathVariable("contactId") String id) {
        ContactResponse contactResponse = contactService.get(user, id);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update (User user,
                                                @RequestBody UpdateContactRequest request,
                                                @PathVariable("contactId") String id) {
        request.setId(id);
        ContactResponse contactResponse = contactService.update(user, request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> update (User user,
                                                @PathVariable("contactId") String id) {
        contactService.delete(user, id);
        return WebResponse.<String>builder().data("Ok").build();
    }
}
