import { Hero, HeroService } from "./heroService";

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
    async consentToPublish() {
        this.heroes.find(h => h.email === this.currentUser!.email)!.published = true;
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
