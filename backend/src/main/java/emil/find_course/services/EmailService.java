package emil.find_course.services;

import java.util.List;

public interface EmailService {

    public void sendHtmlMessage(List<String> to, String subject, String htmlBody);

    public void sendSimpleEmail(List<String> to, String subject, String content);

}
