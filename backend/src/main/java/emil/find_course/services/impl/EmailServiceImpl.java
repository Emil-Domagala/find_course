package emil.find_course.services.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import emil.find_course.services.EmailService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    Dotenv dotenv = Dotenv.load();

    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final String SENDGRID_API_KEY = dotenv.get("SENDGRID_API_KEY");
    private final String MY_EMAIL = dotenv.get("SPRING_MAIL_USERNAME");

    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process(templateName, thymeleafContext);

        Email from = new Email(MY_EMAIL);
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, toEmail, emailContent);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            sg.api(request);
        } catch (IOException e) {
            throw new java.lang.RuntimeException("Exception sending email");
        }
    }

}
