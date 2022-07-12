package com.alkemy.ong.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "springdoc")
@Configuration
public class SwaggerConfiguration {

    private SwaggerUi swaggerUi;
    private ApiDocs apiDocs;

    public String getExtendedPath() {
        return this.swaggerUi.getPath() + "/**";
    }

    public String getExtendedHtmlPath() {
        return this.swaggerUi.getPath().substring(0, this.swaggerUi.getPath().lastIndexOf('/')) + "/swagger-ui/**";
    }

    @Getter
    @Setter
    public static class SwaggerUi {
        private String path;
    }

    @Getter
    @Setter
    public static class ApiDocs {
        private String path;
    }

}
