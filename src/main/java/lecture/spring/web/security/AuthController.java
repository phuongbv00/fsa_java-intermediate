package lecture.spring.web.security;

import lecture.spring.web.dto.LoginReq;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login/jwt")
    public Map<String, Object> login(@RequestBody LoginReq loginReq) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginReq.getEmail());
        if (!passwordEncoder.matches(loginReq.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        String scope = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String accessToken = JwtUtils.generateToken(userDetails.getUsername(), scope);
        return Map.of("accessToken", accessToken);
    }
}
