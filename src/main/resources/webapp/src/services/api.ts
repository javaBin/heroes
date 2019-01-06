
export interface Person {
    name: string;
    email: string;
    twitter?: string;
}

export interface HeroAchievement {
    id?: string;
    type: Achievement;
    label: string;
}

export interface Hero extends Person {
    avatar?: string;
    achievements: HeroAchievement[];
    id?: string;
    achievement?: string;
    published?: boolean;
}

export interface Userinfo {
    authenticated?: boolean;
    admin?: boolean;
    username?: string;
}

export type Achievement = "foredragsholder_jz"|"foredragsholder_javabin"|"styre";

export function allAchievements(): Achievement[] {
    return ["foredragsholder_jz", "foredragsholder_javabin", "styre"];
}

export function achievementName(achievement: Achievement): string {
    switch (achievement) {
    case "foredragsholder_javabin":         return "Foredragsholder JavaBin";
    case "foredragsholder_jz":              return "JavaZone foredragsholder";
    case "styre":                           return "Styre";
    }
}

export interface CreateHeroData {
    people: Person[];
}

interface Heroism {
    achievement: string;
}

interface Consent {
    id: number;
    text: string;
}

export interface HeroProfile {
    profile: Person;
    heroism?: Heroism;
    published?: boolean;
    consent?: Consent;
}

export interface HeroService {
    deleteAchievement(heroId: string, achievementId: string): Promise<void>;
    updateAchievement(heroId: string, achievementId: string, achievement: any): Promise<void>;
    addAchievement(heroId: string, achievement: HeroAchievement): Promise<void>;
    updateHero(heroId: string, update: Partial<Hero>): Promise<void>;
    fetchCreateHeroData(): Promise<CreateHeroData>;
    fetchUserinfo(): Promise<Userinfo>;
    fetchMe(): Promise<HeroProfile>;
    fetchHeroDetails(heroId: string): Promise<Hero>;
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(consentId: number): Promise<void>;
}
