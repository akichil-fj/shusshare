package net.akichil.shusshare.config;

import net.akichil.shusshare.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 認証
        http.formLogin(login -> login
                .loginPage("/login") //自作ログインページ用
                //.loginProcessingUrl("/auth/login")
                .usernameParameter("userId")
                .passwordParameter("password"));

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID"));

        // 認可
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/img/**", "/css/**").permitAll()
                        .requestMatchers("/login", "/logout", "/register/**", "/about/**").permitAll()
                        .requestMatchers("/home", "/mypage").hasAuthority("USER")
                        .anyRequest().authenticated());

        // CSRF Token
        http.csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    @Lazy
    public void configure(AuthenticationManagerBuilder auth,
                          UserDetailsServiceImpl userDetailsService,
                          PasswordEncoder passwordEncoder) throws Exception {
        auth.eraseCredentials(true).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}
