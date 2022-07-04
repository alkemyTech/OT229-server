package com.alkemy.ong.services;

import com.sendgrid.helpers.mail.Mail;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface EmailService {

    String sendEmail(String email,String template)throws IOException;

}
