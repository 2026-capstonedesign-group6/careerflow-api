package com.jobhelper.careerflowapi.global.security.principal;

import com.jobhelper.careerflowapi.global.security.dto.AuthUser;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {

    private final AuthUser authUser;
    private final String password;

    public static PrincipalDetails from(User user) {
        AuthUser authUser = AuthUser.of(user.getId(), user.getEmail(), user.getRole());
        String password = user.getAccounts().stream()
                .map(account -> account.getPassword())
                .filter(pw -> pw != null)
                .findFirst()
                .orElse(null);
        return new PrincipalDetails(authUser, password);
    }

    public static PrincipalDetails from(AuthUser authUser) {
        return new PrincipalDetails(authUser, null);
    }

    public Long getUserId() {
        return authUser.userId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(authUser.role()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return authUser.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
