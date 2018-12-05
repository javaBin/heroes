package no.javabin.heroes;

public class ExceptionUtil {

    public static RuntimeException softenException(Exception e) {
        return helper(e);
    }

    private static <T extends Exception> RuntimeException helper(Exception e) throws T {
        throw (T)e;
    }
}
