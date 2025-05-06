package lecture.spring.web.controller;

import lecture.spring.web.model.User;
import lecture.spring.web.repository.UserRepository;
import lecture.spring.web.security.JwtUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login/jwt")
    public Map<String, Object> loginJwt(@RequestBody LoginReq req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new AuthenticationException("Wrong password") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        String token = JwtUtils.generateToken(String.valueOf(user.getId()), user.getRoles());
        return Map.of("accessToken", token);
    }

    @Getter
    @Setter
    public static class LoginReq {
        private String email;
        private String password;
    }
}
