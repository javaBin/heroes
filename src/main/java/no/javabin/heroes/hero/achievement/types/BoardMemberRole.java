package no.javabin.heroes.hero.achievement.types;

public enum BoardMemberRole {
    BOARD_MEMBER("Board member"), VICE_CHAIR("Vice chair"), CHAIR("Chair");

    private final String description;

    private BoardMemberRole(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
