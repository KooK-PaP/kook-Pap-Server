package com.KooKPaP.server.global.jwt;

import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrincipalDetails implements UserDetails {
    private Long id;
    private SimpleGrantedAuthority authority;
    private LoginType type;


    public Long getId(){
        return id;
    }

    public LoginType getType(){
        return type;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보 반환
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        // 각 멤버들은 하나의 권한만 갖도록 설계됨.
        authorities.add(authority);

        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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
