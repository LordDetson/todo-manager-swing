package by.babanin.todo.view.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import by.babanin.todo.view.about.AboutInfo;

@Configuration
@EnableConfigurationProperties(AboutInfo.class)
public class ViewConfiguration {

    @Value("${application.translation.properties.baseName}")
    private String propertiesBasename;

    @Bean(name = "textsResourceBundleMessageSource")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename(propertiesBasename);
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }
}
