package com.example.trip.security;

import com.example.trip.security.jwt.AuthEntryPointJwt;
import com.example.trip.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Qualifier("customUserDetailsService")
    CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // ─── חדש: ל-Google OAuth2 login ───
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    // ─── חדש: ה-PasswordEncoder מגיע עכשיו מ-PasswordEncoderConfig הנפרד ───
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.env:dev}")
    private String appEnv;

    // ─── חדש ───
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:51024"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean isProd = "prod".equals(appEnv);

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/h2-console/**")
                                .access((authentication, context) -> new AuthorizationDecision(!isProd))

                                .requestMatchers("/api/user/signUp", "/api/user/signIn").permitAll()
                                // ─── חדש: איפוס סיסמה + Google OAuth2 ───
                                .requestMatchers("/api/user/forgot-password", "/api/user/reset-password").permitAll()
                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                                .requestMatchers("/api/user/get").hasRole("ADMIN")
                                .requestMatchers("/api/user/users").hasRole("ADMIN")
                                .requestMatchers("/api/user/me").permitAll()
                                .requestMatchers("/api/category/addCategory").hasRole("ADMIN")
                                .requestMatchers("/api/trip/trips").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/trip/uploadTrip").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/trip/chat").permitAll()
                                .requestMatchers("/api/trip/packingList/{id}").permitAll()
                                .requestMatchers("/api/category/categories").permitAll()
                                .requestMatchers("/api/category/getCategoryById/{id}").permitAll()
                                .requestMatchers("/api/comment/getCommentsByTripsId/**").permitAll()
                                .requestMatchers("/api/comment/comments").permitAll()
                                .requestMatchers("/api/trip/getTripById/{id}").permitAll()
                                .requestMatchers("/api/trip/tripsByCategoryId/{id}").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/user/changeRole/**").hasRole("ADMIN")
                                .requestMatchers("/api/user/banUser/**").hasRole("ADMIN")
                                .requestMatchers("/api/trip/deleteTripByAdmin/**").hasRole("ADMIN")
                                .requestMatchers("/api/trip/admin/dashboard-stats").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/favorite/count/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/rating/byTrip/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/rating/summary/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/trip/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/user/profile/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/directions").authenticated()
                                .anyRequest().authenticated()
                );

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.headers(headers -> headers.frameOptions(frameOption -> frameOption.sameOrigin()));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // ─── חדש: הפעלת Google OAuth2 login ───
        http.oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    // ─── חדש: הדפסת הסיבה האמיתית לכישלון ה-OAuth2 לצורך אבחון ───
                    System.out.println(">>> Google OAuth2 login failed: " + exception.getMessage());
                    exception.printStackTrace();
                    response.sendRedirect(frontendUrl + "/sign-in?error=google");
                })
        );

        return http.build();
    }
}