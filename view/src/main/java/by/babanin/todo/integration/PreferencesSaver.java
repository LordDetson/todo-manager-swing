package by.babanin.todo.integration;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import by.babanin.ext.JsonFileExporter;
import by.babanin.ext.JsonFileImporter;
import by.babanin.ext.preference.PreferencesStore;

@Component
public class PreferencesSaver implements SmartLifecycle {

    private final JsonFileImporter<PreferencesStore> importer;
    private final JsonFileExporter<PreferencesStore> exporter;
    private boolean running;

    public PreferencesSaver(JsonFileImporter<PreferencesStore> importer, JsonFileExporter<PreferencesStore> exporter) {
        this.importer = importer;
        this.exporter = exporter;
    }

    @Override
    public void start() {
        importer.doImport();
        running = true;
    }

    @Override
    public void stop() {
        exporter.doExport(PreferencesStore.getInstance());
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
