
export interface Person {
    name: string;
    email: string;
}

export interface HeroAchievement {
    id?: number;
    type: string;
    label: string;
}

export interface Hero extends Person {
    avatar?: string;
    achievements: HeroAchievement[];
    id?: string;
    achievement?: string;
    published: boolean;
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
    fetchCreateHeroData(): Promise<CreateHeroData>;
    fetchUserinfo(): Promise<Userinfo>;
    fetchMe(): Promise<HeroProfile>;
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(consentId: number): Promise<void>;
}

export class HeroServiceHttp implements HeroService {
    async fetchCreateHeroData(): Promise<CreateHeroData> {
        const response = await fetch("/api/admin/heroes/create");
        if (response.status === 403 || response.status === 401) {
            window.location.href = "/api/login?admin=true";
        }
        return await response.json();
    }
    async fetchUserinfo(): Promise<Userinfo> {
        const response = await fetch("/api/userinfo");
        return await response.json();
    }
    async consentToPublish(consentId: number) {
        await fetch("/api/profiles/mine/consent/1", {
            body: JSON.stringify({consentId}),
            headers: {
                "Content-type": "application/json",
            },
            method: "POST",
        });
    }
    async fetchMe(): Promise<HeroProfile> {
        const response = await fetch("/api/profiles/mine");
        if (response.status === 401) {
            window.location.href = "/api/login";
        }
        return await response.json();
    }
    async addHero(hero: Hero) {
        await fetch("/api/admin/heroes", {
            body: JSON.stringify(hero),
            headers: {
                "Content-type": "application/json",
            },
            method: "POST",
        });
    }
    async fetchHeroes(): Promise<Hero[]> {
        const response = await fetch("/api/heroes");
        return await response.json();
    }

}
