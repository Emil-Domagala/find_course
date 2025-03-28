// package emil.find_course.security;

// import org.springframework.context.annotation.Bean;

// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
// public class SecurityConfig {

//         private final UserDetailsService userDetailsService;
//     private final JwtFilter jwtFilter;

//     @Bean
//     public AuthenticationProvider authProvider() {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(userDetailsService);
//         provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
//         return provider;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         http.csrf(customizer -> customizer.disable());
//         http.authorizeHttpRequests(request -> request
//                 .requestMatchers("register", "login").permitAll()
//                 .anyRequest().authenticated());
//         http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//         return http.build();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }
    
// }
