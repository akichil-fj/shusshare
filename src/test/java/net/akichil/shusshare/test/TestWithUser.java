package net.akichil.shusshare.test;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = TestUserFactory.class)
public @interface TestWithUser {

    int accountId() default 1;

    String userId() default "user";

    String password() default "password";

    String authority() default "USER";
}
