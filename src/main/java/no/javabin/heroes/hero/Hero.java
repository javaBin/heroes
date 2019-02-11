package no.javabin.heroes.hero;

import no.javabin.heroes.hero.achievement.HeroAchievement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Hero {

    private UUID id;

    private String email;

    private String name;

    private String twitter;

    private String avatarImage;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    Long getConsentId() {
        return consentId;
    }

    void setConsentId(Long consentId) {
        this.consentId = consentId;
    }

    String getConsentClientIp() {
        return consentClientIp;
    }

    void setConsentClientIp(String consentClientIp) {
        this.consentClientIp = consentClientIp;
    }

    Instant getConsentedAt() {
        return consentedAt;
    }

    void setConsentedAt(Instant consentedAt) {
        this.consentedAt = consentedAt;
    }

    public List<HeroAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<HeroAchievement> achievements) {
        this.achievements = achievements;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hero)) {
            return false;
        }
        Hero other = (Hero) o;
        return Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{email=" + email + "}";
    }


    public boolean isPublished() {
        return getConsentedAt() != null;
    }
}
