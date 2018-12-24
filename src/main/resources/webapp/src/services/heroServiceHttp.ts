import { CreateHeroData, Hero, HeroAchievement, HeroProfile, HeroService, Userinfo } from "./api";

export class HeroServiceHttp implements HeroService {
    deleteAchievement(heroId: string, achievementId: string): Promise<void> {
        throw new Error("Method not implemented.");
    }
    updateAchievement(heroId: string, achievementId: string, achievement: any): Promise<void> {
        throw new Error("Method not implemented.");
    }
    addAchievement(heroId: string, achievement: HeroAchievement): Promise<void> {
        throw new Error("Method not implemented.");
    }
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
            body: JSON.stringify({ consentId }),
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

    async updateHero(heroId: string, hero: Partial<Hero>) {
        return;
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
