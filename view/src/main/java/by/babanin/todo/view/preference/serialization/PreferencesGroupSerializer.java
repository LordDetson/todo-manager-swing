package by.babanin.todo.view.preference.serialization;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import by.babanin.todo.preferences.Preference;
import by.babanin.todo.preferences.PreferencesGroup;

public class PreferencesGroupSerializer extends StdSerializer<PreferencesGroup> {

    public PreferencesGroupSerializer() {
        super(PreferencesGroup.class);
    }

    @Override
    public void serialize(PreferencesGroup preferencesGroup, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for(Map.Entry<String, Preference> entry : preferencesGroup) {
            gen.writeObjectField(entry.getKey(), entry.getValue());
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(PreferencesGroup preferencesGroup, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(preferencesGroup, JsonToken.START_OBJECT));
        for(Map.Entry<String, Preference> entry : preferencesGroup) {
            gen.writeObjectField(entry.getKey(), entry.getValue());
        }
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
}
