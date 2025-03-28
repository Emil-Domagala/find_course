// package emil.find_course.security;

// import java.io.IOException;

// import org.springframework.web.filter.OncePerRequestFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// public class JwtFilter extends OncePerRequestFilter {

//     private final JwtService jwtService;

//     private final ApplicationContext context;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
//         String authHeader = request.getHeader("Authorization");
//         String token = null;
//         String userName = null;
//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             token = authHeader.substring(7);
//             userName = jwtService.extractUsername(token);
//         }
//         if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

//             UserDetails userDetails = context.getBean(UserDetailsService.class).loadUserByUsername(userName);
//             if (jwtService.validateToken(token, userDetails)) {
//                 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null,
//                         userDetails.getAuthorities());
//                 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                 SecurityContextHolder.getContext().setAuthentication(authToken);

//             }
//         }
//         filterChain.doFilter(request, response);

//     }
    

// }
