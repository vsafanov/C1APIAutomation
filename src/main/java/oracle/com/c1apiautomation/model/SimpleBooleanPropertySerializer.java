package oracle.com.c1apiautomation.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;

public class SimpleBooleanPropertySerializer extends JsonSerializer<SimpleBooleanProperty> {

    @Override
    public void serialize(SimpleBooleanProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeBoolean(value.get());
    }
}

