
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
        if (response.status === 403) {
            window.location.href = "/api/login?admin=true";
        }
        return await response.json();
    }
    async fetchUserinfo(): Promise<Userinfo> {
        const response = await fetch("/api/userinfo");
        return await response.json();
    }
    async consentToPublish() {
        await fetch("/api/profiles/mine/consent/1", {
            body: JSON.stringify({}),
            headers: {
                "Content-type": "application/json",
            },
            method: "POST",
        });
    }
    async fetchMe(): Promise<Hero> {
        const response = await fetch("/api/profiles/mine");
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
