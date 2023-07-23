package security.io.corespringsecurity.security.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;
import security.io.corespringsecurity.repository.UserRepository;
import security.io.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import security.io.corespringsecurity.security.filter.AjaxLoginProcessingFilter;
import security.io.corespringsecurity.security.handler.CustomAccessDeniedHandler;
import security.io.corespringsecurity.security.handler.CustomAuthenticationFailureHandler;
import security.io.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import security.io.corespringsecurity.security.provider.CustomAuthenticationProvider;
import security.io.corespringsecurity.security.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(UserRepository userRepository, AuthenticationConfiguration authenticationConfiguration) {
        this.userRepository = userRepository;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // DB 로직 추가로 인한 인메모리 유저 주석
    /*@Bean
    public UserDetailsService userDetailsService() {

        String password = passwordEncoder().encode("1111");

        UserDetails user = User.builder()
            .username("user")
            .password(password)
            .roles("USER")
            .build();

        UserDetails manager = User.builder()
            .username("manager")
            .password(password)
            .roles("MANAGER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(password)
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, manager, admin);
    }*/

    @Bean
    public UserDetailsService customUserDetailsService(){
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(customUserDetailsService(), passwordEncoder());
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        return web -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**");
    }

    @Bean
    public AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>  formAuthenticationDetailsSource() {
        return new FormAuthenticationDetailsSource();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {

        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");

        return accessDeniedHandler;

    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
        ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManager());
        return ajaxLoginProcessingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> {
                csrf
                    .ignoringRequestMatchers("/api/*");
            })
            .authorizeHttpRequests(requests -> {
                requests
                    .requestMatchers("/", "/users", "/login*").permitAll()
                    .requestMatchers("/mypage").hasRole("USER")
                    .requestMatchers("/messages").hasRole("MANAGER")
                    .requestMatchers("/config").hasRole("ADMIN")
                    .anyRequest().authenticated();
            })
            .formLogin(form -> {
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login_proc")
                    // .defaultSuccessUrl("/", true)
                    // .defaultSuccessUrl("/")
                    .authenticationDetailsSource(formAuthenticationDetailsSource())
                    .successHandler(successHandler())
                    .failureHandler(failureHandler())
                    .permitAll();
            })
            .exceptionHandling(exception -> {
                exception
                    .accessDeniedHandler(accessDeniedHandler());
            })
            .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .build();
    }


}
