import faker from "faker";
import { Hero } from "../src/services/api";
import { MockHeroService } from "../src/services/mockHeroService";

function sampleHero(): Hero {
  return {
    achievements: [],
    email: faker.internet.email(),
    name: faker.name.findName(),
    published: false
  };
}

function adminUser() {
  return {
    admin: true,
    email: faker.internet.email()
  };
}

function normalUser(email?: string) {
  return {
    admin: false,
    email: email || faker.internet.email()
  };
}

describe("hero service with a hero", () => {
  const heroService: MockHeroService = new MockHeroService();
  let newHero: Hero;

  beforeEach(async () => {
    newHero = sampleHero();
    heroService.currentUser = adminUser();
    await heroService.addHero(newHero);
  });

  it("lets hero see themselves", async () => {
    heroService.currentUser = normalUser(newHero.email);
    const me = await heroService.fetchMe();
    expect(me).toEqual({ profile: newHero, achievements: [] });
  });

  it("doesn't display unpublished heroes", async () => {
    heroService.currentUser = normalUser();
    const heroes = await heroService.fetchPublicHeroes();
    expect(heroes.map(h => h.name)).not.toContain(newHero.name);
  });

  it("displays published heroes", async () => {
    heroService.currentUser = normalUser(newHero.email);
    await heroService.consentToPublish();
    heroService.currentUser = normalUser();
    const heroes = await heroService.fetchHeroes();
    expect(heroes.map(h => h.name)).toContain(newHero.name);
  });
});
