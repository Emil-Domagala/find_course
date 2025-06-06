package emil.find_course.auth.confirmEmail;

import emil.find_course.user.entity.User;

public interface ConfirmEmailService {

    public void validateEmail(User user, String token);

    public void sendVerificationEmail(User user);

    String generateConfirmEmailToken(User user);

}
