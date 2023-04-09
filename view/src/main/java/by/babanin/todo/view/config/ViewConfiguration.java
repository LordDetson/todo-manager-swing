package by.babanin.todo.view.config;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import by.babanin.todo.image.IconResources;
import by.babanin.todo.preferences.Preference;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.preferences.PreferencesStore;
import by.babanin.todo.view.about.AboutInfo;
import by.babanin.todo.view.preference.deserialization.PreferencesGroupDeserializer;
import by.babanin.todo.view.preference.deserialization.PreferencesStoreDeserializer;
import by.babanin.todo.view.preference.mixin.DimensionMixIn;
import by.babanin.todo.view.preference.mixin.PreferenceMixIn;
import by.babanin.todo.view.preference.serialization.PreferencesGroupSerializer;
import by.babanin.todo.view.preference.serialization.PreferencesStoreSerializer;

@Configuration
@EnableConfigurationProperties(AboutInfo.class)
public class ViewConfiguration {

    private static final int DEFAULT_MENU_ICON_SIZE = 12;

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

    @Bean
    public FlatSVGIcon appLogoIcon() {
        return IconResources.getIcon("transparent_check_hexagon", DEFAULT_MENU_ICON_SIZE);
    }

    @Bean
    public FlatSVGIcon aboutActionIcon() {
        return getMenuIcon("i");
    }

    @Bean
    public FlatSVGIcon exitIcon() {
        return getMenuIcon("out_door");
    }

    private FlatSVGIcon getMenuIcon(String name) {
        FlatSVGIcon icon = IconResources.getIcon(name, DEFAULT_MENU_ICON_SIZE);
        ColorFilter colorFilter = new ColorFilter();
        colorFilter.add(Color.BLACK, UIManager.getDefaults().getColor("Button.foreground"));
        icon.setColorFilter(colorFilter);
        return icon;
    }

    @Bean
    public PreferencesStore preferencesStore() {
        return PreferencesStore.getInstance();
    }

    @Bean
    public JsonMapper jsonMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(PreferencesStore.class, new PreferencesStoreSerializer());
        module.addDeserializer(PreferencesStore.class, new PreferencesStoreDeserializer());
        module.addSerializer(PreferencesGroup.class, new PreferencesGroupSerializer());
        module.addDeserializer(PreferencesGroup.class, new PreferencesGroupDeserializer());
        return JsonMapper.builder()
                .addModule(module)
                .addMixIn(Preference.class, PreferenceMixIn.class)
                .addMixIn(Dimension.class, DimensionMixIn.class)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
