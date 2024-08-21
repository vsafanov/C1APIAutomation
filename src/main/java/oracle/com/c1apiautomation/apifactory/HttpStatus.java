package oracle.com.c1apiautomation.apifactory;

import java.util.HashMap;
import java.util.Map;

public class HttpStatus {

    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(100, "Continue");
        STATUS_MAP.put(101, "Switching Protocols");
        STATUS_MAP.put(200, "OK");
        STATUS_MAP.put(201, "Created");
        STATUS_MAP.put(202, "Accepted");
        STATUS_MAP.put(203, "Non-Authoritative Information");
        STATUS_MAP.put(204, "No Content");
        STATUS_MAP.put(205, "Reset Content");
        STATUS_MAP.put(206, "Partial Content");
        STATUS_MAP.put(300, "Multiple Choices");
        STATUS_MAP.put(301, "Moved Permanently");
        STATUS_MAP.put(302, "Found");
        STATUS_MAP.put(303, "See Other");
        STATUS_MAP.put(304, "Not Modified");
        STATUS_MAP.put(305, "Use Proxy");
        STATUS_MAP.put(307, "Temporary Redirect");
        STATUS_MAP.put(400, "Bad Request");
        STATUS_MAP.put(401, "Unauthorized");
        STATUS_MAP.put(402, "Payment Required");
        STATUS_MAP.put(403, "Forbidden");
        STATUS_MAP.put(404, "Not Found");
        STATUS_MAP.put(405, "Method Not Allowed");
        STATUS_MAP.put(406, "Not Acceptable");
        STATUS_MAP.put(407, "Proxy Authentication Required");
        STATUS_MAP.put(408, "Request Timeout");
        STATUS_MAP.put(409, "Conflict");
        STATUS_MAP.put(410, "Gone");
        STATUS_MAP.put(411, "Length Required");
        STATUS_MAP.put(412, "Precondition Failed");
        STATUS_MAP.put(413, "Request Entity Too Large");
        STATUS_MAP.put(414, "Request-URI Too Long");
        STATUS_MAP.put(415, "Unsupported Media Type");
        STATUS_MAP.put(416, "Requested Range Not Satisfiable");
        STATUS_MAP.put(417, "Expectation Failed");
        STATUS_MAP.put(500, "Internal Server Error");
        STATUS_MAP.put(501, "Not Implemented");
        STATUS_MAP.put(502, "Bad Gateway");
        STATUS_MAP.put(503, "Service Unavailable");
        STATUS_MAP.put(504, "Gateway Timeout");
        STATUS_MAP.put(505, "HTTP Version Not Supported");
    }

    public static  Map<Integer, String> getCodes()
    {
        return STATUS_MAP;
    }

    public static Map.Entry<Integer,String> getByCode(int code)
    {
        return STATUS_MAP.entrySet().stream().filter(k->k.getKey().equals(code)).findFirst().orElse(null);
    }

    public static String getDescription(int code) {
        return STATUS_MAP.getOrDefault(code, "Unknown Status Code");
    }
}

