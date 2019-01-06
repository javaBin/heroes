package no.javabin.heroes.hero.achievement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.UUID;

import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

public class HeroAchievement {

    public String getLabel() {
        // TODO: Ugh!
        if (type.equals("foredragsholder_jz")) {
            return String.format("Foredragsholder JavaZone %s: %s", data.requiredString("year"), data.requiredString("title"));
        } else if (type.equals("foredragsholder_javabin")) {
            return String.format("Foredragsholder JavaBin %s: %s",
                    LocalDate.parse(data.requiredString("date")).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.forLanguageTag("no"))),
                    data.requiredString("title"));
        } else if (type.equals("styre")) {
            return String.format("Styremeldem JavaBin");
        } else {
            throw new RuntimeException("No support for type " + type);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public void setData(String data) {
        setData(JsonParser.parseToObject(data));
    }

    private UUID id;
    private String type;
    private JsonObject data;

}
