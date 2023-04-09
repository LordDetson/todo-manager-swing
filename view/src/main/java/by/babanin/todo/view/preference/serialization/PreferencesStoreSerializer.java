package by.babanin.todo.view.preference.serialization;

import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import by.babanin.todo.preferences.Preference;
import by.babanin.todo.preferences.PreferencesStore;

public class PreferencesStoreSerializer extends StdSerializer<PreferencesStore> {

    public PreferencesStoreSerializer() {
        super(PreferencesStore.class);
    }

    @Override
    public void serialize(PreferencesStore preferencesStore, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for(Entry<String, Preference> entry : preferencesStore) {
            gen.writeObjectField(entry.getKey(), entry.getValue());
        }
        gen.writeEndObject();
    }
}
