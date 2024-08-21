package oracle.com.c1apiautomation.apifactory;

public enum ContentType {
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_CSV("text/csv"),

    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_GZIP("application/gzip"),
    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel"),
    APPLICATION_VND_OPENXML_SPREADSHEET("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    APPLICATION_VND_MS_POWERPOINT("application/vnd.ms-powerpoint"),
    APPLICATION_VND_OPENXML_PRESENTATION("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT("application/vnd.oasis.opendocument.text"),
    APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET("application/vnd.oasis.opendocument.spreadsheet"),

    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_SVG("image/svg+xml"),

    AUDIO_MPEG("audio/mpeg"),
    AUDIO_WAV("audio/wav"),
    VIDEO_MP4("video/mp4"),
    VIDEO_WEBM("video/webm"),

    MULTIPART_FORM_DATA("multipart/form-data"),
    MULTIPART_ALTERNATIVE("multipart/alternative"),

    APPLICATION_X_JAVA_ARCHIVE("application/x-java-archive"),
    APPLICATION_X_SHOCKWAVE_FLASH("application/x-shockwave-flash");

    private final String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;

    }

    public static ContentType[] getAllValues() {
        return ContentType.values();
    }

    public String getValue() {
        return contentType;
    }

    public String getName() {
        return name();  // This returns the name of the enum constant
    }
}


