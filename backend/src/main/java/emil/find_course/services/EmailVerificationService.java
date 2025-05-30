package emil.find_course.services;

import emil.find_course.domains.entities.user.User;

public interface EmailVerificationService {

    public void validateEmail(User user, String token);

    public void sendVerificationEmail(User user);

    String generateConfirmEmailToken(User user);

}
