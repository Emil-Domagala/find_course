package emil.find_course.auth.emailVerification;

import emil.find_course.user.entity.User;

public interface EmailVerificationService {

    public void validateEmail(User user, String token);

    public void sendVerificationEmail(User user);

    String generateConfirmEmailToken(User user);

}
