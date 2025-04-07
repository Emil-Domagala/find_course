package emil.find_course.services;

import emil.find_course.domains.entities.user.User;

public interface EmailVerificationService {

    public void validateEmail(User user, String token);

    public void resendConfirmEmail(User user);

    public String generateConfirmEmailToken(User user);
}
