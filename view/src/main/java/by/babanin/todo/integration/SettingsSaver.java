package by.babanin.todo.integration;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import by.babanin.ext.JsonFileExporter;
import by.babanin.ext.JsonFileImporter;
import by.babanin.ext.settings.Settings;

@Component
public class SettingsSaver implements SmartLifecycle {

    private final JsonFileImporter<Settings> settingsImporter;
    private final JsonFileExporter<Settings> settingsExporter;
    private boolean running;

    public SettingsSaver(JsonFileImporter<Settings> settingsImporter, JsonFileExporter<Settings> settingsExporter) {
        this.settingsImporter = settingsImporter;
        this.settingsExporter = settingsExporter;
    }

    @Override
    public void start() {
        settingsImporter.doImport();
        running = true;
    }

    @Override
    public void stop() {
        settingsExporter.doExport(Settings.getInstance());
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
