import React from "react";
import renderer, { ReactTestInstance, ReactTestRenderer } from "react-test-renderer";
import { App, HeroList } from "../src/app";
import { AdminScreen } from "../src/app/admin";
import { HeroControlPanel, HeroListView } from "../src/app/admin/HeroControlPanel";
import { Hero, Person } from "../src/services/heroService";
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
            {name: "Alice", email: "alice@example.com", achievements: [], achievement: "", published: true, id: "1"},
            {name: "Bob", email: "bob@example.com", achievements: [], achievement: "", published: true, id: "2"},
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

describe("HeroControlPanel", () => {

    const heroes: Hero[] = [
        {
            achievements: [], email: "johannes@example.com", id: "1", name: "Johannes Test", published: false,
        },
        {
            achievements: [], email: "test@example.com", id: "2", name: "Some test", published: true,
        },
    ];

    const people: Person[] = [
        { email: "johannes@example.com", name: "Johannes Brodwall" },
        { email: "alice@example.com", name: "Alice" },
        { email: "bob@example.net", name: "Bob" },
    ];

    it("shows list of current heroes", async () => {
        const app = renderer.create(<HeroControlPanel heroes={heroes} prefix="#test" people={people} />);
        await promiseCompletion();
        expect(app.toJSON()).toMatchSnapshot();
        expect(app.root.findByType(HeroListView).findByType("ul").findAllByType("a").map(li => li.children[0]))
            .toEqual(["Johannes Test", "Some test"]);
    });

});
