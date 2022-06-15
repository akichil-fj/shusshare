package net.akichil.shusshare.test;

import net.akichil.shusshare.security.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class TestUserFactory implements WithSecurityContextFactory<TestWithUser> {

    @Override
    public SecurityContext createSecurityContext(TestWithUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Integer accountId = annotation.accountId();
        String userId = annotation.userId();
        String password = annotation.password();
        String authority = annotation.authority();

        LoginUser loginUser = new LoginUser(accountId, userId, password, AuthorityUtils.createAuthorityList(authority));
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPassword(), loginUser.getAuthorities());

        context.setAuthentication(token);
        return context;
    }

}
