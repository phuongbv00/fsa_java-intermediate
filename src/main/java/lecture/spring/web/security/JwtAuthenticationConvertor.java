package lecture.spring.web.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class JwtAuthenticationConvertor implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = request.getHeader("Authorization").substring(7);
        return new UsernamePasswordAuthenticationToken(null, token);
    }
}
