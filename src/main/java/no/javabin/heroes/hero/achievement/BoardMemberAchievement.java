package no.javabin.heroes.hero.achievement;

import java.time.Year;
import java.util.Objects;

import org.jsonbuddy.JsonObject;

public class BoardMemberAchievement extends HeroAchievement {

    public BoardMemberAchievement() {
        setType(Achievement.STYRE);
    }

    private Year year;

    private BoardMemberRole role;

    public final Year getYear() {
        return year;
    }

    public final void setYear(Year year) {
        this.year = year;
    }

    public final BoardMemberRole getRole() {
        return role;
    }

    public final void setRole(BoardMemberRole role) {
        this.role = role;
    }

    @Override
    public String getLabel() {
        return String.format("%s JavaBin %s", getRole().description(), getYear());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoardMemberAchievement)) {
            return false;
        }
        BoardMemberAchievement other = (BoardMemberAchievement) o;
        return Objects.equals(id, other.id) && Objects.equals(year, other.year)
                && Objects.equals(role, other.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, role);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ",year=" + year + ",role=" + role + "}";
    }

    @Override
    protected void setFields(JsonObject json) {
        json.put("year", year.toString()).put("role", role);
    }

    @Override
    protected HeroAchievement readFields(JsonObject json) {
        setRole(json.requiredEnum("role", BoardMemberRole.class));
        setYear(Year.parse(json.requiredString("year")));
        return this;
    }
}
