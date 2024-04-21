package msyaipulanwar.restful.resolver;

import jakarta.servlet.http.HttpServletRequest;
import msyaipulanwar.restful.entity.User;
import msyaipulanwar.restful.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(UserArgumentResolver.class);
    private final String unauthorized;
    private final UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
        unauthorized = "Unauthorized";
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");

        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorized);
        }

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorized));

        log.warn("system : {}, current : {}", System.currentTimeMillis(), user.getTokenExpired());
        if(System.currentTimeMillis()*1000 > user.getTokenExpired()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorized);
        }

        return user;
    }
}
