
package oracle.com.c1apiautomation.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonIgnore
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
