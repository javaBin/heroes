package no.javabin.heroes;

import java.time.Instant;
import java.util.Objects;

public class Hero {

    private String email;

    private String achievement;

    private Long consentId;

    private String consentClientIp;

    private Instant consentedAt;

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
