package lecture.spring.web.security;

import jakarta.annotation.PostConstruct;
import lecture.spring.web.model.User;
import lecture.spring.web.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class DefaultUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0) return;
        userRepository.save(User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin"))
                .roles(List.of("ADMIN", "USER"))
                .build());
        userRepository.save(User.builder()
                .email("user1@example.com")
                .password(passwordEncoder.encode("user1"))
                .roles(List.of("USER"))
                .build());
        userRepository.save(User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("user2"))
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(u -> new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return u.getRoles().stream().map(SimpleGrantedAuthority::new).toList();
                    }

                    @Override
                    public String getPassword() {
                        return u.getPassword();
                    }

                    @Override
                    public String getUsername() {
                        return u.getEmail();
                    }
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
