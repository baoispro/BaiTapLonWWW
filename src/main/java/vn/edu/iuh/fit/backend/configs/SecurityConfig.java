package vn.edu.iuh.fit.backend.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.iuh.fit.backend.constants.RoleConstant;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        String pass = passwordEncoder().encode("123");
        UserDetails user1 = User.builder()
                .username("root")
                .password("$2a$12$AC3oYMywkKh0Hda6q0NXkOQIRN68bP1PIA.SToyZz9ULvX5s9ORlC")
                .roles(RoleConstant.ADMIN)
                .build();
        auth.inMemoryAuthentication().withUser(user1);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(
                authorizeRequests ->
                        authorizeRequests.requestMatchers("/","/index.html","/home","/static/**","/css/**").permitAll()
                                .requestMatchers("/candidates").hasRole("ADMIN")


        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
