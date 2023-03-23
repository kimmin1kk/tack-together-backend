package com.dnlab.tacktogetherbackend.auth.common;

import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.user.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Member member;
    private List<Authority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities
                .stream()
                .map(authority -> (GrantedAuthority) () -> authority.getAuthorityName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabledAccount();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabledAccount();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabledAccount();
    }

    @Override
    public boolean isEnabled() {
        return isEnabledAccount();
    }

    private boolean isEnabledAccount() {
        return member.isEnabled();
    }
}
