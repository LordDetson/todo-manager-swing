package by.babanin.todo.view.about;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("about")
public class AboutInfo {

    private String country;
    private final ProductInfo product = new ProductInfo();
    private final LicenseInfo license = new LicenseInfo();
    private final List<Developer> developers = new ArrayList<>();
    private final List<Contributor> contributors = new ArrayList<>();

    @Getter
    @Setter
    public static class ProductInfo {

        private String name;
        private String description;
        private String url;
        private String inceptionYear;
    }

    @Getter
    @Setter
    public static class LicenseInfo {

        private String name;
        private String url;
    }

    @Getter
    @Setter
    public static class Developer {

        private String name;
        private String email;
        private String github;
        private String linkedin;
        private String telegram;
    }

    @Getter
    @Setter
    public static class Contributor {

        private String name;
        private String email;
        private String linkedin;
        private String telegram;
    }
}
