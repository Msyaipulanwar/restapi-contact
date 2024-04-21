package msyaipulanwar.restful.service;

import org.springframework.transaction.annotation.Transactional;
import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.model.LoginUserRequest;
import msyaipulanwar.restful.model.TokenResponse;
import msyaipulanwar.restful.repository.UserRepository;
import msyaipulanwar.restful.security.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ValidationService validationService;

    public AuthService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password Wrong"));

        if(!BCrypt.checkpw(request.getPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password Wrong");
        }

        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpired(next30Days());
        userRepository.save(user);

        return TokenResponse.builder()
                .token(user.getToken())
                .expireAt(user.getTokenExpired())
                .build();
    }

    private Long next30Days(){
        return System.currentTimeMillis() * (1000) * 60;
    }

    @Transactional
    public void logout(User user){
        user.setTokenExpired(null);
        user.setToken(null);
        userRepository.save(user);
    }
}
