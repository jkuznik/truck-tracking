package pl.jkuznik.trucktracking.security.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final String[] WHITE_LIST = {
            "swagger-ui.html",
            "swagger-ui/index.html"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().authenticated())
                .headers(headers ->
                        headers.frameOptions(Customizer.withDefaults()).disable())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var userDetailsManager = new InMemoryUserDetailsManager();

        var user1 = User.withUsername("fire")
                .password(passwordEncoder().encode("tms"))
                .roles("USER")
                .build();

        userDetailsManager.createUser(user1);

        return userDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
