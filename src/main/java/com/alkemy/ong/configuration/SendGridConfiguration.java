package com.alkemy.ong.configuration;
import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;


@Configuration
public class SendGridConfiguration {

    @Value("${app.sendgrid.key}")
    private String appKey;

    @Bean
    public SendGrid getSendGrid(){
        return new SendGrid(appKey);
    }

    @Bean
    @Primary
    public FreeMarkerConfigurationFactoryBean factoryBean(){
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("classpath:/templates");
        return bean;
    }

}
