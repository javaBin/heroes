package no.javabin.infrastructure;

public class ExceptionUtil {

    public static RuntimeException softenException(Exception e) {
        return helper(e);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> RuntimeException helper(Exception e) throws T {
        throw (T)e;
    }
}
