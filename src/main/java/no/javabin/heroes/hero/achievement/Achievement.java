package no.javabin.heroes.hero.achievement;

public enum Achievement {

    FOREDRAGSHOLDER_JZ {
        @Override
        public HeroAchievement newInstance() {
            return new ConferenceSpeakerAchievement();
        }
    }
    ,
    FOREDRAGSHOLDER_JAVABIN {
        @Override
        public HeroAchievement newInstance() {
            return new UsergroupSpeakerAchievement();
        }
    }
    ,
    STYRE {
        @Override
        public HeroAchievement newInstance() {
            return new BoardMemberAchievement();
        }
    };

    public abstract HeroAchievement newInstance();
}
