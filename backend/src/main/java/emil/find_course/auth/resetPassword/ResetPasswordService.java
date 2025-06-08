package emil.find_course.auth.resetPassword;

import emil.find_course.user.entity.User;

public interface ResetPasswordService {

    public void resetPassword( String token, String password);

    public void sendResetPasswordEmail(String email);

    String generateResetPasswordToken(User user);
}
