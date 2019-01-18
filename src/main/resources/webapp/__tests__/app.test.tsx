import React from "react";
import renderer, { ReactTestInstance, ReactTestRenderer } from "react-test-renderer";
import { App, HeroList } from "../src/app";
import { HeroControlPanel } from "../src/app/admin/HeroControlPanel";
import { MockHeroService } from "../src/services/mockHeroService";

function promiseCompletion() {
    return new Promise((resolve) => setImmediate(resolve));
}

const mockHeroService = new MockHeroService();
jest.mock("../src/services/heroServiceHttp", () => ({
    HeroServiceHttp: () => mockHeroService,
}));

// tslint:disable-next-line:no-eval
eval(`global["window"] = {
    location: {
        hash: ""
    },
    addEventListener() {},
    removeEventListener() {}
}`);

function findComponent(ancestor: ReactTestRenderer, type: React.ReactType): ReactTestInstance {
    expect(ancestor.root.findAll(t => true).map(t => t.type)).toContain(type);
    return ancestor.root.findByType(type);

}

describe("app", () => {
    it("renders hero list", async () => {
        const heroes = [
            {name: "Alice", email: "alice@example.com", achievements: [], achievement: "", published: true, id: "1"},
            {name: "Bob", email: "bob@example.com", achievements: [], achievement: "", published: true, id: "2"},
        ];
        mockHeroService.fetchPublicHeroes = async () => heroes;
        const app = renderer.create(<App heroService={mockHeroService} />);
        await promiseCompletion();
        expect(findComponent(app, HeroList).props).toHaveProperty("heroes", heroes);
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("renders admin page", async () => {
        window.location.hash = "#admin";
        const app = renderer.create(<App heroService={mockHeroService} />);
        await promiseCompletion();

        expect(findComponent(app, HeroControlPanel).props).toHaveProperty("heroService", mockHeroService);
        expect(app.toJSON()).toMatchSnapshot();
    });
});
