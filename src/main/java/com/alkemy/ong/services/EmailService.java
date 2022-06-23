package com.alkemy.ong.services;

import com.sendgrid.helpers.mail.Mail;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface EmailService {

    String sendEmail(String email)throws IOException;
    Mail prepareMail(String email) throws TemplateException, IOException;

    String prepareWelcomeTemplate(String email) throws IOException, TemplateException;

}
