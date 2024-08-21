package oracle.com.c1apiautomation.utils;

import java.util.Arrays;

public class ExceptionHandler {

    public static String getFilteredStackTrace(Throwable e, String packageName) {
        return e.getMessage() + "\n\n" + Arrays.stream(e.getStackTrace())
                .filter(element -> element.getClassName().startsWith(packageName))
                .map(element -> element.getClassName() + "." + element.getMethodName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")")
                .reduce("", (acc, element) -> acc + element + "\n");
    }
}
