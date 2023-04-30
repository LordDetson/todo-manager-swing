package by.babanin.todo.view.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.json.JsonMapper;

import by.babanin.todo.preferences.PreferenceException;
import by.babanin.todo.view.util.AppUtils;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class SettingsLifecycle implements SmartLifecycle {

    private final ApplicationContext applicationContext;
    private Settings settings;
    private final JsonMapper jsonMapper;

    @Value("${settings.directory:${app.workdir}/settings}")
    private Path directory;

    @Value("${settings.filename:settings}")
    private String fileName;

    private boolean running;

    public SettingsLifecycle(
            ApplicationContext applicationContext,
            Settings settings,
            @Qualifier("settings") JsonMapper jsonMapper) {
        this.applicationContext = applicationContext;
        this.settings = settings;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void start() {
        Path settingsFilePath = directory.resolve(fileName);
        log.info("Reading settings from {}", settingsFilePath);
        try {
            if(Files.exists(settingsFilePath)) {
                Settings deserializeSettings = jsonMapper.readValue(settingsFilePath.toFile(), Settings.class);
                DefaultSingletonBeanRegistry beanRegistry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
                beanRegistry.destroySingleton("settings");
                beanRegistry.registerSingleton("settings", deserializeSettings);
                this.settings = deserializeSettings;
            }
        }
        catch(IOException e) {
            throw new PreferenceException(e);
        }
        finally {
            running = true;
        }
    }

    @Override
    public void stop() {
        try {
            Path settingsFilePath = directory.resolve(fileName);
            log.info("Writing settings to {}", settingsFilePath);
            AppUtils.writeObjectToFile(jsonMapper, settingsFilePath, settings);
        }
        catch(IOException e) {
            throw new PreferenceException(e);
        }
        finally {
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
