package net.akichil.shusshare.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;

public enum AccountRole {

    USER() {
        @Override
        List<GrantedAuthority> getGradAuthority() {
            return AuthorityUtils.createAuthorityList("USER");
        }
    };

    abstract List<GrantedAuthority> getGradAuthority();
}
