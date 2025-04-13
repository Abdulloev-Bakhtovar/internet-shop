package ru.bakht.internetshop.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.TokenType;
import ru.bakht.internetshop.auth.model.enums.RoleName;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;
import ru.bakht.internetshop.auth.repository.RoleRepo;
import ru.bakht.internetshop.auth.repository.TokenTypeRepo;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner runner(RoleRepo roleRepo,
                                    TokenTypeRepo tokenTypeRepo) {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                if (roleRepo.findByName(roleName).isEmpty()) {
                    roleRepo.save(
                            Role.builder()
                                    .name(roleName)
                                    .build()
                    );
                }
            }

            for (TokenTypeName tokenTypeName : TokenTypeName.values()) {
                if (tokenTypeRepo.findByName(tokenTypeName).isEmpty()) {
                    tokenTypeRepo.save(
                            TokenType.builder()
                                    .name(tokenTypeName)
                                    .build()
                    );
                }
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
