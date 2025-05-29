package emil.find_course.common.security.jwt;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import emil.find_course.domains.entities.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;

@Data
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public UUID getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    public boolean isEmailVerified() {
        return user.isEmailVerified();
    }

}
