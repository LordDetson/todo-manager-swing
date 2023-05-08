package by.babanin.todo.integration;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.regex.Matcher;

import javax.swing.Icon;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import by.babanin.ext.JsonFileExporter;
import by.babanin.ext.JsonFileImporter;
import by.babanin.ext.component.action.Action;
import by.babanin.ext.component.util.GUIUtils;
import by.babanin.ext.message.TranslateCode;
import by.babanin.ext.message.Translator;
import by.babanin.ext.preference.PreferencesGroup;
import by.babanin.ext.preference.PreferencesStore;
import by.babanin.ext.preference.deserialization.PreferencesGroupDeserializer;
import by.babanin.ext.preference.deserialization.PreferencesStoreDeserializer;
import by.babanin.ext.preference.mixin.DimensionMixIn;
import by.babanin.ext.preference.serialization.PreferencesGroupSerializer;
import by.babanin.ext.preference.serialization.PreferencesStoreSerializer;
import by.babanin.ext.settings.Settings;
import by.babanin.ext.settings.SettingsDialog;
import by.babanin.ext.settings.SettingsPanel;
import by.babanin.ext.settings.SettingsPublisher;
import by.babanin.ext.settings.deserialization.SettingsDeserializer;
import by.babanin.ext.settings.serialization.SettingsSerializer;
import by.babanin.todo.image.IconResources;
import by.babanin.todo.view.about.AboutInfo;
import by.babanin.todo.view.util.AppUtils;

@Configuration
@EnableConfigurationProperties({
        AboutInfo.class
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
        Translator.setMessageProvider(() -> (code, locale) -> rs.getMessage(code, null, locale));
        Translator.setLocaleSupplier(LocaleContextHolder::getLocale);
        return rs;
    }

    @Bean
    public Image appLogoImage() {
        return IconResources.getIcon("transparent_check_hexagon", AppUtils.DEFAULT_MENU_ICON_SIZE).getImage();
    }

    @Bean
    public Icon aboutActionIcon() {
        return AppUtils.getMenuIcon("i");
    }

    @Bean
    public Icon exitIcon() {
        return AppUtils.getMenuIcon("out_door");
    }

    @Bean
    public Icon settingsIcon() {
        return AppUtils.getMenuIcon("gearwheel");
    }

    @Bean
    public Icon errorIcon() {
        return IconResources.getIcon("error", 48);
    }

    @Bean
    public PreferencesStore preferencesStore() {
        return PreferencesStore.getInstance();
    }

    @Bean
    public ConversionService conversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(String.class, Path.class,
                source -> Path.of(source.replaceFirst("^~", Matcher.quoteReplacement(System.getProperty("user.home")))));
        return conversionService;
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
                .addMixIn(Dimension.class, DimensionMixIn.class)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Bean
    @Qualifier("preferences")
    public JsonFileExporter<PreferencesStore> preferencesExporter(
            @Value("${preferences.directory}") Path directory,
            @Value("${preferences.filename}") String fileName
    ) {
        return new JsonFileExporter<>(preferencesJsonMapper(), directory.resolve(fileName));
    }

    @Bean
    @Qualifier("preferences")
    public JsonFileImporter<PreferencesStore> preferencesImporter(
            @Value("${preferences.directory}") Path directory,
            @Value("${preferences.filename}") String fileName
    ) {
        return new JsonFileImporter<>(preferencesJsonMapper(), directory.resolve(fileName), PreferencesStore.class);
    }

    @Bean
    public Action showSettingsDialogAction(SettingsPublisher settingsPublisher) {
        return Action.builder()
                .id("showSettingsDialogAction")
                .name(Translator.toLocale(TranslateCode.MAIN_MENU_SETTINGS))
                .smallIcon(settingsIcon())
                .mnemonic(KeyEvent.VK_S)
                .action(actionEvent -> settingsDialog(settingsPublisher).setVisible(true))
                .build();
    }

    @Bean
    public Settings settings() {
        return Settings.getInstance();
    }

    @Bean
    public SettingsPublisher settingsPublisher(SettingsSubscriberDelegator delegator) {
        SettingsPublisher settingsPublisher = new SettingsPublisher();
        settingsPublisher.subscribe(delegator);
        return settingsPublisher;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SettingsDialog settingsDialog(SettingsPublisher settingsPublisher) {
        return new SettingsDialog(GUIUtils.getMainWindow(), settingsPanel(settingsPublisher));
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SettingsPanel settingsPanel(SettingsPublisher settingsPublisher) {
        return new SettingsPanel(settingsPublisher, settings());
    }

    @Bean
    @Qualifier("settings")
    public JsonMapper settingsJsonMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Settings.class, new SettingsSerializer());
        module.addDeserializer(Settings.class, new SettingsDeserializer());
        return JsonMapper.builder()
                .addModule(module)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Bean
    @Qualifier("settings")
    public JsonFileExporter<Settings> settingsExporter(
            @Value("${settings.directory}") Path directory,
            @Value("${settings.filename}") String fileName
    ) {
        return new JsonFileExporter<>(settingsJsonMapper(), directory.resolve(fileName));
    }

    @Bean
    @Qualifier("settings")
    public JsonFileImporter<Settings> settingsImporter(
            @Value("${settings.directory}") Path directory,
            @Value("${settings.filename}") String fileName
    ) {
        return new JsonFileImporter<>(settingsJsonMapper(), directory.resolve(fileName), Settings.class);
    }
}
