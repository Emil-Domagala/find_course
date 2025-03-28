// package emil.find_course.security.jwt;

// import emil.find_course.domains.entities.user.User;
// import emil.find_course.repositories.UserRepository;

// // @Service
// @RequiredArgsConstructor
// public class UserDetailsServiceImpl implements UserDetailsService {

//     private final UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//         User user = userRepository.findByEmail(email);
//         if (user == null) {
//             throw new UsernameNotFoundException("User not found");
//         }
//         return new UserPrincipal(user);
//     }

// }