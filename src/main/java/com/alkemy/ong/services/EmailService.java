package com.alkemy.ong.services;
import java.io.IOException;
import com.alkemy.ong.configuration.SendGridConfiguration;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;


@Service
public class EmailService implements ImpEmailService {

    @Autowired
    private SendGridConfiguration config;

    @Autowired
    private SendGrid sendGrid;

    @Override
    public String sendEmail(String email) throws IOException {

        try {
            Mail mail = prepareMail(email);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
        } catch (IOException e) {
            throw new IOException("Cannot send email");
        }
        return "mail has beent sent check your inbox";

    }

    public Mail prepareMail(String email) throws IOException {

        Mail mail = new Mail();
        Email fromEmail = new Email();
        Content content = new Content();

        content.setType("text/html");
        content.setValue("esto es un mail de prueba");

        fromEmail.setEmail("ot229alkemy@gmail.com");
        mail.setFrom(fromEmail);
        Email to = new Email();
        to.setEmail(email);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        mail.addContent(content);
        mail.setSubject("Email test");
        mail.addPersonalization(personalization);

        return mail;

    }


}