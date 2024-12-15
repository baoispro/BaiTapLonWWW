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

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("select username, password, enabled from users where username = ?")
                .authoritiesByUsernameQuery("select username, role from users where username = ?");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(
                authorizeRequests ->
                {
                    try {
                        authorizeRequests.requestMatchers("/","/index.html","/home","/static/**","/css/**","/images/**","/jobs/**","/companies/**","/company/**","/signUp").permitAll()
                                .requestMatchers("/candidates").hasRole(RoleConstant.ADMIN)
                                .requestMatchers("/dang-tin-tuyen-dung").hasAnyRole(RoleConstant.ADMIN, RoleConstant.RECRUITER)
                                .anyRequest().authenticated()// sau khi xong nhớ xóa dùng để debug
                                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/") // URL sau khi logout thành công
                                    .invalidateHttpSession(true) // Invalidate session
                                    .clearAuthentication(true) // Clear authentication data
                            .       permitAll();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }


        ).formLogin();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
