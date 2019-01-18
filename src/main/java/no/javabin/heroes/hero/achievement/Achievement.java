package no.javabin.heroes.hero.achievement;

import no.javabin.heroes.hero.achievement.types.BoardMemberAchievement;
import no.javabin.heroes.hero.achievement.types.BoardMemberAchievementDao;
import no.javabin.heroes.hero.achievement.types.ConferenceSpeakerAchievement;
import no.javabin.heroes.hero.achievement.types.ConferenceSpeakerAchievementDao;
import no.javabin.heroes.hero.achievement.types.UsergroupSpeakerAchievement;
import no.javabin.heroes.hero.achievement.types.UsergrupSpeakerAchievementDao;
import org.fluentjdbc.DbContext;

public enum Achievement {

    FOREDRAGSHOLDER_JZ {
        @Override
        public HeroAchievement newInstance() {
            return new ConferenceSpeakerAchievement();
        }

        @Override
        public HeroAchievementDao<?> getDao(DbContext context) {
            return new ConferenceSpeakerAchievementDao(context);
        }
    }
    ,
    FOREDRAGSHOLDER_JAVABIN {
        @Override
        public HeroAchievement newInstance() {
            return new UsergroupSpeakerAchievement();
        }

        @Override
        public HeroAchievementDao<?> getDao(DbContext context) {
            return new UsergrupSpeakerAchievementDao(context);
        }
    }
    ,
    STYRE {
        @Override
        public HeroAchievement newInstance() {
            return new BoardMemberAchievement();
        }

        @Override
        public HeroAchievementDao<?> getDao(DbContext context) {
            return new BoardMemberAchievementDao(context);
        }
    };

    public abstract HeroAchievement newInstance();

    public abstract HeroAchievementDao<?> getDao(DbContext context);
}
