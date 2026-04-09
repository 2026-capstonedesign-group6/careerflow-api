package com.jobhelper.careerflowapi.global.security.token;

import com.jobhelper.careerflowapi.global.security.principal.PrincipalDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final PrincipalDetails principal;
    private final String credentials;

    public JwtAuthenticationToken(
            PrincipalDetails principal,
            String credentials,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
