package security.io.corespringsecurity.security.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import security.io.corespringsecurity.repository.UserRepository;
import security.io.corespringsecurity.security.filter.AjaxLoginProcessingFilter;
import security.io.corespringsecurity.security.handler.AjaxAuthenticationFailureHandler;
import security.io.corespringsecurity.security.handler.AjaxAuthenticationSuccessHandler;
import security.io.corespringsecurity.security.handler.CustomAuthenticationFailureHandler;
import security.io.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import security.io.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import security.io.corespringsecurity.security.service.CustomUserDetailsService;

@Configuration
@Order(0)
public class AjaxSecurityConfig {

    private final UserRepository userRepository;

    private final AuthenticationConfiguration authenticationConfiguration;

    public AjaxSecurityConfig(UserRepository userRepository, AuthenticationConfiguration authenticationConfiguration) {
        this.userRepository = userRepository;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService customUserDetailsService(){
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AjaxAuthenticationProvider ajaxAuthenticationProvider(){
        return new AjaxAuthenticationProvider(customUserDetailsService(), passwordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        ProviderManager authenticationManager = (ProviderManager)authenticationConfiguration.getAuthenticationManager();
        authenticationManager.getProviders().add(ajaxAuthenticationProvider());

        return authenticationManager;
    }

    @Bean
    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
        ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        ajaxLoginProcessingFilter.setAuthenticationSuccessHandler(ajaxSuccessHandler());
        ajaxLoginProcessingFilter.setAuthenticationFailureHandler(ajaxFailureHandler());
        return ajaxLoginProcessingFilter;
    }

    @Bean
    public AuthenticationSuccessHandler ajaxSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler ajaxFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    @Bean
    @Order(0)
    public SecurityFilterChain ajaxSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
            .authorizeHttpRequests(requests -> {
                requests
                    .anyRequest().authenticated();
            })
            .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf -> {
                csrf
                    .ignoringRequestMatchers("/api/**");
            })
            .build();
    }
}
