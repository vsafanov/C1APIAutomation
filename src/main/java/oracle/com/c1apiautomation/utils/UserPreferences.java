package oracle.com.c1apiautomation.utils;

import java.util.prefs.Preferences;
import java.util.Map;
import java.util.HashMap;

public class UserPreferences {

    public static final String USER_THEME_KEY = "userTheme";
    public final static String DARK_THEME = "/DarkTheme.css";
    public final static String LIGHT_THEME = "";

    private static UserPreferences instance;
    private final Preferences preferences;

    private UserPreferences() {
        preferences = Preferences.userNodeForPackage(UserPreferences.class);
    }

    public static synchronized UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }

    // Methods to get/set individual preferences
    public void setString(String key, String value) {
        preferences.put(key, value);
    }

    public String getString(String key, String defaultValue) {
        return preferences.get(key, defaultValue);
    }

    public void setInt(String key, int value) {
        preferences.putInt(key, value);
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    // Method to get all preferences as a Map
    public Map<String, String> getAllPreferences() {
        Map<String, String> allPrefs = new HashMap<>();
        try {
            for (String key : preferences.keys()) {
                allPrefs.put(key, preferences.get(key, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allPrefs;
    }
}

