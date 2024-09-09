package oracle.com.c1apiautomation.utils;

/******************************************
 * OSPlatform Class
 ******************************************/
public class OSPlatform {

    private static String platform;

    static {
        platform = System.getProperty("javafx.platform","desktop").toUpperCase();
    }

    public static boolean isAndroid() {
        return platform.equals("ANDROID");
    } // isAndroid()

    public static boolean isDesktop() {
        return platform.equals("DESKTOP");
    } // isDesktop()

    public static boolean isIOS() {
        return platform.equals("IOS");
    } // isIOS()
} // class OSPlatform
