
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

export enum Achievement {
    foredragsholder_jz, foredragsholder_javabin, styre,
}

export function achievementName(achievement: Achievement): string {
    switch (achievement) {
    case Achievement.foredragsholder_javabin:   return "Foredragsholder JavaBin";
    case Achievement.foredragsholder_jz:        return "JavaZone foredragsholder";
    case Achievement.styre:                     return "Styre";
    }
}

export function allAchievements(): Achievement[] {
    return Object.keys(Achievement)
        .filter(key => !isNaN(Number((Achievement as any)[key])))
        .map((s: string) => (Achievement as any)[s]);
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
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(consentId: number): Promise<void>;
}
