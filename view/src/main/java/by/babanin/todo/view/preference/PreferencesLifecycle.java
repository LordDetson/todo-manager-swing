package by.babanin.todo.view.preference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.json.JsonMapper;

import by.babanin.todo.preferences.PreferenceException;
import by.babanin.todo.preferences.PreferencesStore;
import by.babanin.todo.view.util.AppUtils;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class PreferencesLifecycle implements SmartLifecycle {

    private final PreferencesStore preferencesStore;
    private final JsonMapper jsonMapper;

    @Value("${preferences.directory:${app.workdir}/preferences}")
    private Path directory;

    @Value("${preferences.filename:preferences}")
    private String fileName;

    private boolean running;

    public PreferencesLifecycle(PreferencesStore preferencesStore, @Qualifier("preferences") JsonMapper jsonMapper) {
        this.preferencesStore = preferencesStore;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void start() {
        Path preferencesFilePath = directory.resolve(fileName);
        log.info("Reading preferences from {}", preferencesFilePath);
        try {
            if(Files.exists(preferencesFilePath)) {
                jsonMapper.readValue(preferencesFilePath.toFile(), PreferencesStore.class);
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
            Path preferencesFilePath = directory.resolve(fileName);
            log.info("Writing preferences to {}", preferencesFilePath);
            AppUtils.writeObjectToFile(jsonMapper, preferencesFilePath, preferencesStore);
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
