package winter.di.util;

public class SnowdropUtil {
    private SnowdropUtil() {
    }

    public static String decapitalize(String str) {
        if (str.isBlank()) {
            throw new RuntimeException("wtf u doing");
        }
        var decapitalizedFstLtr = str.substring(0, 1).toLowerCase();
        if (str.length() == 1) return decapitalizedFstLtr;
        return decapitalizedFstLtr + str.substring(1);
    }
}
