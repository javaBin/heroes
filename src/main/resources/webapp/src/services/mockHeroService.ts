import { Hero, HeroAchievement, HeroService } from "./api";

interface User {
    admin: boolean;
    email: string;
}

export class MockHeroService implements HeroService {
    currentUser?: User = {
        admin: true,
        email: "johannes@brodwall.com",
    };

    private heroes: Hero[] = [
        {
            achievements: [],
            email: this.currentUser!.email,
            id: "abc123",
            name: "Johannes",
        },
    ];

    deleteAchievement(heroId: string, achievementId: string): Promise<void> {
        throw new Error("Method not implemented.");
    }
    updateAchievement(heroId: string, achievementId: string, achievement: any): Promise<void> {
        throw new Error("Method not implemented.");
    }
    addAchievement(heroId: string, achievement: HeroAchievement): Promise<void> {
        throw new Error("Method not implemented.");
    }
    async fetchCreateHeroData() {
        return {
            achievements: [],
            people: [],
        };
    }
    async consentToPublish() {
        this.heroes.find(h => h.email === this.currentUser!.email)!.published = true;
    }

    async updateHero(heroId: string, update: Partial<Hero>) {
        // const heroIndex = this.heroes.findIndex(h => h.id === heroId);
        // this.heroes[heroIndex] = {...this.heroes[heroIndex], ...update};
    }

    async fetchUserinfo() {
        return {
            authenticated: !!this.currentUser,
            username: this.currentUser && this.currentUser.email,
        };
    }

    async fetchMe() {
        return {profile: this.heroes.find(h => h.email === this.currentUser!.email)!};
    }
    async addHero(hero: Hero) {
        this.heroes.push(hero);
    }
    async fetchPublicHeroes() {
        return this.heroes.filter(h => h.published);
    }

    async fetchHeroes() {
        return this.heroes;
    }

    async fetchHeroDetails(id: string) {
        const heroes = await this.fetchHeroes();
        return heroes.find(h => h.id === id)!;
    }

}
