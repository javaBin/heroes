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
        if (pathInfo.equals("/achievement")) {
            return ServletOperation.ALL_ACHIEVEMENTS;
        }
        if (pathInfo.startsWith("/achievements/")) {
            return ServletOperation.READ_SINGLE_ACHIVEMENT;
        }
        return ServletOperation.UNKNOWN;
    }

    public ServletOperation computePost(String pathInfo) {
        if (pathInfo == null) {
            return ServletOperation.UNKNOWN;
        }
        if (pathInfo.equals("/person")) {
            return ServletOperation.ADD_PERSON;
        }
        if (pathInfo.equals("/achievement")) {
            return ServletOperation.ADD_ACHIEVEMENT;
        }
        return ServletOperation.UNKNOWN;
    }
}
