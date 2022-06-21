package com.alkemy.ong.configuration;
import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SendGridConfiguration {

    @Value("${app.sendgrid.key}")
    private String appKey;

    @Bean
    public SendGrid getSendGrid(){
        return new SendGrid(appKey);
    }


}
