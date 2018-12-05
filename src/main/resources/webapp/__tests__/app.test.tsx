import React, { ReactInstance } from "react";
import renderer, { ReactTestInstance, ReactTestRenderer } from "react-test-renderer";
import { App, HeroList } from "../src/app";
import { AdminScreen } from "../src/app/admin";
import { MockHeroService } from "../src/services/mockHeroService";

function promiseCompletion() {
    return new Promise((resolve) => setImmediate(resolve));
}

const mockHeroService = new MockHeroService();

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
            {name: "Alice", email: "alice@example.com", published: true},
            {name: "Bob", email: "bob@example.com", published: true},
        ];
        mockHeroService.fetchHeroes = async () => heroes;
        const app = renderer.create(<App heroService={mockHeroService} />);
        await promiseCompletion();
        expect(findComponent(app, HeroList).props).toHaveProperty("heroes", heroes);
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("renders admin page", async () => {
        window.location.hash = "#admin";
        const app = renderer.create(<App heroService={mockHeroService} />);
        await promiseCompletion();

        expect(findComponent(app, AdminScreen).props).toHaveProperty("heroService", mockHeroService);
        expect(app.toJSON()).toMatchSnapshot();
    });
});
