package emil.find_course.common.service;

import java.util.Map;

public interface EmailService {

  public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel);

}
