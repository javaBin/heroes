package no.javabin.heroes.hero.achievement;

import java.time.Year;
import java.util.Objects;

import org.jsonbuddy.JsonObject;

public class ConferenceSpeakerAchievement extends HeroAchievement {

    public ConferenceSpeakerAchievement() {
        setType(Achievement.FOREDRAGSHOLDER_JZ);
    }

    private Year year;

    private String title;

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getLabel() {
        return String.format("Foredragsholder JavaZone %s: %s", getYear(), getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConferenceSpeakerAchievement)) {
            return false;
        }
        ConferenceSpeakerAchievement other = (ConferenceSpeakerAchievement) o;
        return Objects.equals(id, other.id) && Objects.equals(title, other.title)
                && Objects.equals(year, other.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ",title=" + title + ",year=" + year + "}";
    }

    @Override
    protected void setFields(JsonObject json) {
        json.put("title", getTitle()).put("year", getYear().toString());
    }

    @Override
    protected HeroAchievement readFields(JsonObject json) {
        setTitle(json.requiredString("title"));
        setYear(Year.parse(json.requiredString("year")));
        return this;
    }

}
