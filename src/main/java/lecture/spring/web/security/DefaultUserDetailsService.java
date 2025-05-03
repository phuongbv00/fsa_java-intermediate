package lecture.spring.web.security;

import lecture.spring.web.model.User;
import lecture.spring.web.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        seedUsers();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(DefaultUserDetails::fromUser)
                .orElse(null);
    }

    private void seedUsers() {
        userRepository.save(User.builder()
                .email("root@example.com")
                .password(passwordEncoder.encode("root"))
                .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                .build());
        userRepository.save(User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin"))
                .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                .build());
        userRepository.save(User.builder()
                .email("user1@example.com")
                .password(passwordEncoder.encode("user1"))
                .roles(List.of("ROLE_USER"))
                .build());
        userRepository.save(User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("user2"))
                .roles(List.of("ROLE_USER"))
                .build());
    }
}
