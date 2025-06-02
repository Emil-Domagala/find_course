package emil.find_course.auth.email;

import emil.find_course.user.entity.User;

public interface EmailVerificationService {

    public void validateEmail(User user, String token);

    public void sendVerificationEmail(User user);

    String generateConfirmEmailToken(User user);

}
