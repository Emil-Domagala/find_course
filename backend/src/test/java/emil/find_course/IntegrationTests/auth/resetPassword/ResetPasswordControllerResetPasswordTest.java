package emil.find_course.IntegrationTests.auth.resetPassword;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ResetPasswordControllerResetPasswordTest extends IntegrationTestBase {

// Sucess returns no content changes user password, deletes resetPasswordOTT
// Couldnt find OOT by token 
// OOT Expired
// User not verified
// Old password cant be this same as old one
// Invalid password should be rejected (to short, long, null)


}
