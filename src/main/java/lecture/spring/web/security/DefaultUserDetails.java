package lecture.spring.web.security;

import lecture.spring.web.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class DefaultUserDetails implements UserDetails {
    private String username;
    private String password;
    private List<String> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public static UserDetails fromUser(User user) {
        DefaultUserDetails userDetails = new DefaultUserDetails();
        userDetails.username = user.getEmail();
        userDetails.password = user.getPassword();
        userDetails.authorities = user.getRoles();
        return userDetails;
    }
}
