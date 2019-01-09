package no.javabin.heroes.hero.achievement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;

import org.jsonbuddy.JsonObject;

public class UsergroupSpeakerAchievement extends HeroAchievement {

    public UsergroupSpeakerAchievement() {
        setType(Achievement.FOREDRAGSHOLDER_JAVABIN);
    }

    private LocalDate date;

    private String title;

    public final LocalDate getDate() {
        return date;
    }

    public final void setDate(LocalDate date) {
        this.date = date;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getLabel() {
        return String.format("Foredragsholder JavaBin %s: %s",
                getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.forLanguageTag("no"))),
                getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UsergroupSpeakerAchievement)) {
            return false;
        }
        UsergroupSpeakerAchievement other = (UsergroupSpeakerAchievement) o;
        return Objects.equals(id, other.id) && Objects.equals(title, other.title)
                && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, date);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ",title=" + title + ",date=" + date + "}";
    }

    @Override
    protected void setFields(JsonObject json) {
        json.put("title", getTitle()).put("date", getDate());
    }

    @Override
    protected HeroAchievement readFields(JsonObject json) {
        setTitle(json.stringValue("title").orElse(null));
        setDate(json.stringValue("date").map(LocalDate::parse).orElse(null));
        return this;
    }

}
