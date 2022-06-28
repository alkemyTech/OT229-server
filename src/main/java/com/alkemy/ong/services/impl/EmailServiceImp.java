package com.alkemy.ong.services.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alkemy.ong.services.EmailService;
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
    public String sendEmail(String email) throws IOException{

        try {
            Mail mail = prepareMail(email);
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

    public Mail prepareMail(String email) throws IOException,TemplateException {

        Mail mail = new Mail();
        Email fromEmail = new Email();
        Content content = new Content();

        content.setType("text/html");
        content.setValue(prepareWelcomeTemplate(email));

        fromEmail.setEmail("ot229alkemy@gmail.com");
        mail.setFrom(fromEmail);
        Email to = new Email();
        to.setEmail(email);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        mail.addContent(content);
        mail.setSubject("Bienvenid@");

        mail.addPersonalization(personalization);

        return mail;

    }

    @Override
    public String prepareWelcomeTemplate(String email) throws IOException, TemplateException {
        Map<String,Object> model = new HashMap<>();
        model.put("email",email);
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("plantilla_email.html"),model);
    }
}