
export interface Person {
    name: string;
    email: string;
}

export interface Hero extends Person {
    achievement?: string;
    published: boolean;
}

export interface Userinfo {
    authenticated?: boolean;
    admin?: boolean;
    username?: string;
}

export interface Achievement {
    value: string;
    label: string;
}

export interface CreateHeroData {
    people: Person[];
    achievements: Achievement[];
}

export interface HeroService {
    fetchCreateHeroData(): Promise<CreateHeroData>;
    fetchUserinfo(): Promise<Userinfo>;
    fetchMe(): Promise<Hero>;
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(): Promise<void>;
}

export class HeroServiceHttp implements HeroService {
    async fetchCreateHeroData(): Promise<CreateHeroData> {
        const response = await fetch("/api/admin/heroes/create");
        return await response.json();
    }
    async fetchUserinfo(): Promise<Userinfo> {
        const response = await fetch("/api/userinfo");
        return await response.json();
    }
    consentToPublish(): Promise<void> {
        throw new Error("Method not implemented.");
    }
    async fetchMe(): Promise<Hero> {
        throw new Error("Method not implemented.");
    }
    addHero(hero: Hero): Promise<void> {
        throw new Error("Method not implemented.");
    }
    async fetchHeroes(): Promise<Hero[]> {
        return [];
    }

}
