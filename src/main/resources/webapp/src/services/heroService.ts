
export interface Hero {
    name: string;
    email: string;
    contribution?: string;
    published: boolean;
}

export interface HeroService {
    fetchMe(): Promise<Hero>;
    fetchHeroes(): Promise<Hero[]>;
    addHero(hero: Hero): Promise<void>;
    consentToPublish(): Promise<void>;
}

export class HeroServiceHttp implements HeroService {
    consentToPublish(): Promise<void> {
        throw new Error("Method not implemented.");
    }
    fetchMe(): Promise<Hero> {
        throw new Error("Method not implemented.");
    }
    addHero(hero: Hero): Promise<void> {
        throw new Error("Method not implemented.");
    }
    async fetchHeroes(): Promise<Hero[]> {
        return [];
    }

}
