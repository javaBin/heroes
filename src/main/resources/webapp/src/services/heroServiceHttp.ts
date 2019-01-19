import { CreateHeroData, Hero, HeroAchievement, HeroProfile, HeroService, Userinfo } from "./api";

export class HeroServiceHttp implements HeroService {
  async deleteAchievement(heroId: string, achievementId: string) {
    await fetch("/api/admin/heroes/" + heroId + "/achievements/" + achievementId, { method: "DELETE" });
  }
  async updateAchievement(heroId: string, achievementId: string, achievement: any) {
    await this.sendJSON("/api/admin/heroes/" + heroId + "/achievements/" + achievementId, achievement, "PUT");
  }
  async addAchievement(heroId: string, achievement: HeroAchievement): Promise<void> {
    await this.sendJSON("/api/admin/heroes/" + heroId + "/achievements", achievement);
  }
  async fetchCreateHeroData(): Promise<CreateHeroData> {
    const response = await fetch("/api/admin/heroes/create");
    if (response.status === 403 || response.status === 401) {
      window.location.href = "/api/login?admin=true";
    }
    return await response.json();
  }

  async fetchHeroDetails(id: string) {
    const response = await fetch("/api/heroes/" + id);
    return await response.json();
  }
  async fetchUserinfo(): Promise<Userinfo> {
    const response = await fetch("/api/userinfo");
    return await response.json();
  }
  async consentToPublish(consentId: number) {
    await this.sendJSON("/api/profiles/mine/consent/1", { consentId });
  }
  async fetchMe(): Promise<HeroProfile> {
    const response = await fetch("/api/profiles/mine");
    if (response.status === 401) {
      window.location.href = "/api/login";
    }
    return await response.json();
  }

  async updateHero(heroId: string, hero: Partial<Hero>) {
    await this.sendJSON("/api/admin/heroes/" + heroId, hero, "PUT");
  }

  async addHero(hero: Hero) {
    await this.sendJSON("/api/admin/heroes", hero);
  }
  async fetchHeroes(): Promise<Hero[]> {
    const response = await fetch("/api/admin/heroes");
    return await response.json();
  }

  async fetchPublicHeroes(): Promise<Hero[]> {
    const response = await fetch("/api/heroes");
    return await response.json();
  }

  async sendJSON(path: string, json: any, method: string = "POST") {
    await fetch(path, {
      body: JSON.stringify(json),
      headers: {
        "Content-type": "application/json"
      },
      method
    });
  }
}
