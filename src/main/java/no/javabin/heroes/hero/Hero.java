package no.javabin.heroes.hero;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import no.javabin.heroes.hero.achievement.HeroAchievement;

public class Hero {

    private UUID id;

    private String email;

    private String achievement;

    private Long consentId;

    private String consentClientIp;

    private Instant consentedAt;

    private List<HeroAchievement> achievements = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public Long getConsentId() {
        return consentId;
    }

    public void setConsentId(Long consentId) {
        this.consentId = consentId;
    }

    public String getConsentClientIp() {
        return consentClientIp;
    }

    public void setConsentClientIp(String consentClientIp) {
        this.consentClientIp = consentClientIp;
    }

    public Instant getConsentedAt() {
        return consentedAt;
    }

    public void setConsentedAt(Instant consentedAt) {
        this.consentedAt = consentedAt;
    }

    public List<HeroAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<HeroAchievement> achievements) {
        this.achievements = achievements;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hero)) {
            return false;
        }
        Hero other = (Hero) o;
        return Objects.equals(email, other.email) && Objects.equals(achievement, other.achievement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, achievement);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{email=" + email + ",achievement=" + achievement + "}";
    }


}
