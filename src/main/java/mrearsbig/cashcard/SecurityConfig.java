package mrearsbig.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // This method configures the security filter chain for the application.
        // It can be customized to define security rules, such as authentication and
        // authorization.
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/cashcards/**")
                .hasRole("CARD-OWNER"))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build(); // Replace with actual security filter chain configuration
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // This method provides a PasswordEncoder bean for encoding passwords.
        // It can be customized to use different password encoding strategies.
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        // This method provides a UserDetailsService bean for testing purposes.
        // It can be customized to define test users with specific roles and passwords.
        User.UserBuilder users = User.builder();

        UserDetails sarah = users
                .username("sarah1")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER")
                .build();

        UserDetails hankOwnsNoCards = users
                .username("hank-owns-no-cards")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER")
                .build();

        return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards);
    }
}
