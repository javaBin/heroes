
export interface Person {
    name: string;
    email: string;
    twitter?: string;
}

export interface HeroAchievement {
    id?: string;
    type: Achievement;
    label?: string;
}

export interface Hero extends Person {
    avatar?: string;
    achievements: HeroAchievementDetail[];
    id?: string;
    achievement?: string;
    published?: boolean;
}

export interface Userinfo {
    authenticated?: boolean;
    admin?: boolean;
    username?: string;
}

export type Achievement = "FOREDRAGSHOLDER_JZ"|"FOREDRAGSHOLDER_JAVABIN"|"STYRE";

export function allAchievements(): Achievement[] {
    return ["FOREDRAGSHOLDER_JZ", "FOREDRAGSHOLDER_JAVABIN", "STYRE"];
}

export function achievementName(achievement: Achievement): string {
    switch (achievement) {
    case "FOREDRAGSHOLDER_JAVABIN":         return "Foredragsholder JavaBin";
    case "FOREDRAGSHOLDER_JZ":              return "JavaZone foredragsholder";
    case "STYRE":                           return "Styre";
    }
}

export interface CreateHeroData {
    people: Person[];
}

interface ConferenceSpeakerAchievement extends HeroAchievement {
    type: "FOREDRAGSHOLDER_JZ";
    year: number;
    title: string;
}

interface UsergroupSpeakerAchievement extends HeroAchievement {
    type: "FOREDRAGSHOLDER_JAVABIN";
    date: Date;
    title: string;
}

export type BoardMemberRole = "BOARD_MEMBER" | "VICE_CHAIR" | "CHAIR";

export function boardMemberRoleName(role: BoardMemberRole): string {
    switch (role) {
        case "BOARD_MEMBER": return "Board member";
        case "CHAIR": return "Chair";
        case "VICE_CHAIR": return "Vice chair";
    }
}

interface BoardMemberAchivement extends HeroAchievement {
    type: "STYRE";
    year: number;
    role: BoardMemberRole;
}

export type HeroAchievementDetail = ConferenceSpeakerAchievement | UsergroupSpeakerAchievement | BoardMemberAchivement;

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
    updateAchievement(heroId: string, achievementId: string, achievement: HeroAchievementDetail): Promise<void>;
    addAchievement(heroId: string, achievement: HeroAchievementDetail): Promise<void>;
    updateHero(heroId: string, update: Partial<Hero>): Promise<void>;
    fetchCreateHeroData(): Promise<CreateHeroData>;
    fetchUserinfo(): Promise<Userinfo>;
    fetchMe(): Promise<HeroProfile>;
    fetchHeroDetails(heroId: string): Promise<Hero>;
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(consentId: number): Promise<void>;
}
