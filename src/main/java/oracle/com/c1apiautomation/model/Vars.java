package oracle.com.c1apiautomation.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class Vars {
    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }


    // Method to get by key
    public String getByKey(String key) {
        return properties.get(key); // returns null if key is not found
    }

    public String getByKey(String key, boolean caseInsensitive) {
        if (!caseInsensitive) {
            return properties.get(key);
        }
        // Convert the map to a stream and perform a case-insensitive lookup
        return properties.entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public String getKeyByValue(String value) {
        return getKeyByValue(value, false);
    }

    // Method to get key by value using stream and filter
    public String getKeyByValue(String value, boolean caseInsensitive) {
        return properties.entrySet()
                .stream()
                .filter(e -> caseInsensitive ? e.getValue().equalsIgnoreCase(value) : e.getValue().equals(value))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null); // Return null if no matching value is found
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
