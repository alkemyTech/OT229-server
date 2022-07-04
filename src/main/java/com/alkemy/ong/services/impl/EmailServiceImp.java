package com.alkemy.ong.services.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alkemy.ong.services.EmailService;
import com.alkemy.ong.utility.GlobalConstants;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;


@Service
public class EmailServiceImp implements EmailService {


    @Autowired
    private Configuration configuration;

    @Autowired
    private SendGrid sendGrid;

    @Override
    public String sendEmail(String email,String template) throws IOException{

        try {
            Mail mail = prepareMail(email,template);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new IOException("Cannot send email");
        }
        return "mail has beent sent check your inbox";

    }

    private Mail prepareMail(String email,String template) throws IOException,TemplateException {

        Mail mail = new Mail();
        Email fromEmail = new Email();
        Content content = new Content();

        fromEmail.setEmail("ot229alkemy@gmail.com");
        mail.setFrom(fromEmail);
        Email to = new Email();
        to.setEmail(email);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        content.setType("text/html");

        if (template.equals(GlobalConstants.TEMPLATE_WELCOME)) {
            content.setValue(prepareWelcomeTemplate());
            mail.setSubject("ONG: Welcome!");
        }else if (template.equals(GlobalConstants.TEMPLATE_CONTACT)) {
            content.setValue(prepareContactTemplate());
            mail.setSubject("ONG: Contact Form");
        }else {
            content.setValue(template);
            mail.setSubject("ONG OT229");
        }
        mail.addContent(content);
        mail.addPersonalization(personalization);

        return mail;

    }

    private String prepareContactTemplate() throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put("title", GlobalConstants.TITLE_EMAIL_CONTACT);
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("plantilla_email.html"),model);
    }

    private String prepareWelcomeTemplate() throws IOException, TemplateException {
        Map<String,Object> model = new HashMap<>();
        model.put("title",GlobalConstants.TITLE_EMAIL_WELCOME );
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("plantilla_email.html"),model);
    }
}