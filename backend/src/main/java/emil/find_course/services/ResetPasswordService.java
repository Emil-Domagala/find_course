package emil.find_course.services;

import emil.find_course.domains.entities.user.User;

public interface ResetPasswordService {

    public void resetPassword(String token, String password);

    public void sendResetPasswordEmail(String email);

    String generateResetPasswordToken(User user);
}
