package com.alkemy.ong.services;

import com.sendgrid.helpers.mail.Mail;
import java.io.IOException;

public interface EmailService {

    String sendEmail(String email)throws IOException;
    Mail prepareMail(String email) throws IOException;

}
