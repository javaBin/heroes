import { CreateHeroData, Hero, HeroService } from "./heroService";

interface User {
    admin: boolean;
    email: string;
}

export class MockHeroService implements HeroService {
    currentUser?: User = {
        admin: true,
        email: "johannes@brodwall.com",
    };

    private heroes: Hero[] = [];

    async fetchCreateHeroData() {
        return {
            achievements: [],
            people: [],
        };
    }
    async consentToPublish() {
        this.heroes.find(h => h.email === this.currentUser!.email)!.published = true;
    }

    async fetchUserinfo() {
        return {
            authenticated: !!this.currentUser,
            username: this.currentUser && this.currentUser.email,
        };
    }

    async fetchMe() {
        return this.heroes.find(h => h.email === this.currentUser!.email)!;
    }
    async addHero(hero: Hero) {
        this.heroes.push(hero);
    }
    async fetchHeroes() {
        return this.heroes.filter(h => h.published);
    }

}
