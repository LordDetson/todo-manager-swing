package by.babanin.todo.view.config;

import java.awt.Dimension;
import java.awt.Image;
import java.nio.file.Path;
import java.util.regex.Matcher;

import javax.swing.Icon;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
import by.babanin.todo.view.settings.Settings;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

@Configuration
@EnableConfigurationProperties({
        AboutInfo.class,
        Settings.class
})
public class ViewConfiguration {

    @Value("${application.translation.properties.baseName}")
    private String propertiesBasename;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename(propertiesBasename);
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        Translator.setMessageSource(rs);
        return rs;
    }

    @Bean
    public Image appLogoImage() {
        return IconResources.getIcon("transparent_check_hexagon", GUIUtils.DEFAULT_MENU_ICON_SIZE).getImage();
    }

    @Bean
    public Icon aboutActionIcon() {
        return GUIUtils.getMenuIcon("i");
    }

    @Bean
    public Icon exitIcon() {
        return GUIUtils.getMenuIcon("out_door");
    }

    @Bean
    public Icon settingsIcon() {
        return GUIUtils.getMenuIcon("gearwheel");
    }

    @Bean
    public PreferencesStore preferencesStore() {
        return PreferencesStore.getInstance();
    }

    @Bean
    @Qualifier("preferences")
    public JsonMapper preferencesJsonMapper() {
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

    @Bean
    @Qualifier("settings")
    public JsonMapper settingsJsonMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    public ConversionService conversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(String.class, Path.class,
                source -> Path.of(source.replaceFirst("^~", Matcher.quoteReplacement(System.getProperty("user.home")))));
        return conversionService;
    }
}
