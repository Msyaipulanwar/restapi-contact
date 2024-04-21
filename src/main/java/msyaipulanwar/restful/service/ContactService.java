package msyaipulanwar.restful.service;

import msyaipulanwar.restful.model.UpdateContactRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import msyaipulanwar.restful.entity.Contact;
import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.model.ContactResponse;
import msyaipulanwar.restful.model.CreateContactRequest;
import msyaipulanwar.restful.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    private final ValidationService validationService;

    public ContactService(ContactRepository contactRepository, ValidationService validationService) {
        this.contactRepository = contactRepository;
        this.validationService = validationService;
    }


    @Transactional
    public ContactResponse create (User user, CreateContactRequest request){
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);
        contactRepository.save(contact);

        return toResponse(contact);
    }

    private ContactResponse toResponse(Contact contact){
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id){
        Contact contact  = contactRepository.findFirstByUserAndId(user, id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found")
        );
        return toResponse(contact);
    }
    
    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found")
        );

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return toResponse(contact);
    }

    @Transactional
    public void delete(User user, String id){

        Contact contact = contactRepository.findFirstByUserAndId(user, id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found")
        );

        contactRepository.delete(contact);
    }
}
