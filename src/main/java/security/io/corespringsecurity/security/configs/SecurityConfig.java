package security.io.corespringsecurity.security.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;
import security.io.corespringsecurity.repository.UserRepository;
import security.io.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import security.io.corespringsecurity.security.factory.UrlResourcesMapFactoryBean;
import security.io.corespringsecurity.security.handler.CustomAccessDeniedHandler;
import security.io.corespringsecurity.security.handler.CustomAuthenticationFailureHandler;
import security.io.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import security.io.corespringsecurity.security.manager.CustomRequestMatcherDelegatingAuthorizationManager;
import security.io.corespringsecurity.security.provider.CustomAuthenticationProvider;
import security.io.corespringsecurity.security.service.CustomUserDetailsService;
import security.io.corespringsecurity.security.service.SecurityResourceService;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private SecurityResourceService securityResourceService;

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public CustomRequestMatcherDelegatingAuthorizationManager customAuthorizationManager() throws Exception {
        return new CustomRequestMatcherDelegatingAuthorizationManager(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {

        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    @Bean
    public AuthorizationFilter customAuthorizationFilter() throws Exception {
        return new AuthorizationFilter(customAuthorizationManager());
    }

    @Bean
    @Order(0)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(requests -> {
                requests
                    .requestMatchers("/", "/users", "/login*", "/css/**", "/js/**", "/images/**", "/error/**").permitAll()
                    // .requestMatchers("/mypage").hasAnyRole("USER", "ADMIN")
                    // .requestMatchers("/messages").hasRole("MANAGER")
                    // .requestMatchers("/config").hasRole("ADMIN")
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
            .authenticationProvider(customAuthenticationProvider())
            .addFilterBefore(customAuthorizationFilter(), AuthorizationFilter.class)
            .build();
    }
}
