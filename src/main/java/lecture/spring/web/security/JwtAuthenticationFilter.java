package lecture.spring.web.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null) {
            filterChain.doFilter(request, response);
        } else {
            header = header.trim();
            if (!StringUtils.startsWithIgnoreCase(header, "Bearer")) {
                filterChain.doFilter(request, response);
            } else if (header.equalsIgnoreCase("Bearer")) {
                throw new BadCredentialsException("Empty JWT token");
            } else {
                String token = header.substring(7);
                Claims claims = JwtUtils.verifyToken(token);
                assert claims != null;
                Long uid = Long.valueOf(claims.getSubject());
                List<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles", String.class).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(uid, token, authorities));
                filterChain.doFilter(request, response);
            }
        }

    }
}
