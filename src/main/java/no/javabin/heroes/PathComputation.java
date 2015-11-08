package no.javabin.heroes;

public class PathComputation {
    public ServletOperation computeGet(String pathInfo) {
        if (pathInfo == null) {
            return ServletOperation.UNKNOWN;
        }
        if (pathInfo.equals("/person")) {
            return ServletOperation.ALL_PERSONS;
        }
        if (pathInfo.startsWith("/person/")) {
            return ServletOperation.READ_SINGLE_PERSON;
        }
        return ServletOperation.UNKNOWN;
    }
}
