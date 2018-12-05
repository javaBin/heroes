
export interface Hero {
    name: string;
    email: string;
    contribution?: string;
    published: boolean;
}

export interface Userinfo {
    authenticated?: boolean;
    admin?: boolean;
    username?: string;
}

export interface HeroService {
    fetchUserinfo(): Promise<Userinfo>;
    fetchMe(): Promise<Hero>;
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(): Promise<void>;
}

export class HeroServiceHttp implements HeroService {
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
