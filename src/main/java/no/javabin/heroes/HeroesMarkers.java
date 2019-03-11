package no.javabin.heroes;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class HeroesMarkers {
    public static final Marker RESTART = MarkerFactory.getMarker("RESTART");
    public static final Marker OPS = MarkerFactory.getMarker("OPS");

    static {
        OPS.add(RESTART);
    }
}
