package oracle.com.c1apiautomation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.beans.property.SimpleBooleanProperty;

public class JsonConfig {

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addSerializer(SimpleBooleanProperty.class, new SimpleBooleanPropertySerializer());
        mapper.registerModule(module);

        return mapper;
    }
}
