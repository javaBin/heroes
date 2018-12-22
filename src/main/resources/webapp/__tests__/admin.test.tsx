import React from "react";
import renderer, { ReactTestRenderer } from "react-test-renderer";
import {
    AddHeroAchievement, AddHeroView, BoardMemberAchievementDetails,
    HeroAchievementList,
    HeroControlPanel, HeroListView,
    HeroView,
    JavaBinSpeakerAchievementDetails,
    JavaZoneSpeakerAchievementDetails,
} from "../src/app/admin/HeroControlPanel";
import { Achievement, Hero, Person } from "../src/services/heroService";

// tslint:disable-next-line:no-eval
eval(`global["window"] = {
    location: {
        hash: ""
    },
    addEventListener() {},
    removeEventListener() {}
}`);

function promiseCompletion() {
    return new Promise((resolve) => setImmediate(resolve));
}

describe("HeroControlPanel", () => {

    const heroes: Hero[] = [
        {
            achievements: [
                { id: "13", label: "ABC", type: Achievement.foredragsholder_javabin },
                { id: "13", label: "XYZ", type: Achievement.foredragsholder_javabin },
            ],
            email: "johannes@example.com", id: "1", name: "Johannes Test", published: false,
        },
        {
            achievements: [
                {id: "11", label: "A great guy!", type: Achievement.foredragsholder_javabin },
                {id: "12", label: "A super guy!", type: Achievement.styre },
            ],
            email: "test@example.com", id: "2", name: "Some test", published: true,
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

    it("shows hero status", async () => {
        window.location.hash = "#test/heroes/" + heroes[1].id;

        const app = renderer.create(<HeroControlPanel heroes={heroes} prefix="#test" people={people} />);
        await promiseCompletion();
        expect(app.root.findByType(HeroView).findByType("h2").children[0]).toBe(heroes[1].name);
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("shows new hero screen", async () => {
        window.location.hash = "#test/heroes/add";

        const app = renderer.create(<HeroControlPanel heroes={heroes} prefix="#test" people={people} />);
        await promiseCompletion();
        expect(app.root.findByType(AddHeroView).findByType("h2").children[0]).toBe("Add a hero");
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("adds hero from slack list", async () => {
        window.location.hash = "#test/heroes/add";

        const app = renderer.create(<HeroControlPanel heroes={heroes} prefix="#test" people={people} />);
        await promiseCompletion();

        app.root.findByType("select").props.onChange({target: {value: people[1].email}});
        app.root.findByType(AddHeroView).findByType("form").props.onSubmit({preventDefault: jest.fn()});
        await promiseCompletion();

        expect(app.root.instance.state.heroes.map((h: Hero) => h.name)).toContain(people[1].name);
    });

    describe("adds achievement to hero", async () => {
        let app: ReactTestRenderer;
        beforeEach(async () => {
            window.location.hash = "#test/heroes/" + heroes[1].id + "/addAchievement";

            app = renderer.create(<HeroControlPanel heroes={heroes} prefix="#test" people={people} />);
            await promiseCompletion();
        });

        describe("of JavaZone achievement type", () => {
            beforeEach(async () => {
                app.root.findByType(AddHeroAchievement).instance
                .setState({achievementType: Achievement.foredragsholder_jz});
                await promiseCompletion();
            });

            it("shows achievement form", async () => {
                app.root.findByType(JavaZoneSpeakerAchievementDetails);
                expect(app.toJSON()).toMatchSnapshot();
            });

            it("creates new achievement", async () => {
                const form = app.root.findByType(AddHeroAchievement).findByType("form");
                const titleInput = form.findByType("input");
                const submitButton = form.findByType("button");
                titleInput.props.onChange({ target: { value: "My Talk"}});
                submitButton.props.onClick({preventDefault: jest.fn()});
                await promiseCompletion();

                expect(app.root.instance.state.heroes[1].achievements
                    .map((a: any) => a.title)).toContain("My Talk");
            });
        });

        it("shows JavaBin achievement type", async () => {
            app.root.findByType(AddHeroAchievement).instance
                .setState({achievementType: Achievement.foredragsholder_javabin});
            await promiseCompletion();
            app.root.findByType(JavaBinSpeakerAchievementDetails);
            expect(app.toJSON()).toMatchSnapshot();
        });

        it("shows board member achievement type", async () => {
            app.root.findByType(AddHeroAchievement).instance
                .setState({achievementType: Achievement.styre});
            await promiseCompletion();
            app.root.findByType(BoardMemberAchievementDetails);
            expect(app.toJSON()).toMatchSnapshot();
        });
    });

    it("shows hero edit view", async () => {
        window.location.hash = "#test/heroes/" + heroes[1].id + "/edit";

        const app = renderer.create(<HeroControlPanel heroes={heroes} people={people} prefix="#test" />);
        const [displayName, email, twitter] = app.root.findByType("form").findAllByType("input");
        expect(displayName.props.value).toEqual(heroes[1].name);
        expect(email.props.value).toEqual(heroes[1].email);
        expect(twitter.props.value).toEqual(heroes[1].twitter);

        expect(app.toJSON()).toMatchSnapshot();
    });

    it("udpates hero name", async () => {
        window.location.hash = "#test/heroes/" + heroes[0].id + "/edit";

        const app = renderer.create(<HeroControlPanel heroes={heroes} people={people} prefix="#test" />);
        const [displayName] = app.root.findByType("form").findAllByType("input");
        await displayName.props.onChange({target: {value: "Updated name"}});
        app.root.findByType("form").props.onSubmit({preventDefault: jest.fn()});

        expect(heroes[0].name).toEqual("Updated name");

        expect(app.toJSON()).toMatchSnapshot();
    });

    it("lists achievements", async () => {
        window.location.hash = "#test/heroes/" + heroes[1].id;

        const app = renderer.create(<HeroControlPanel heroes={heroes} people={people} prefix="#test" />);
        expect(app.root.findByType(HeroAchievementList).findAllByType("li").map(li => li.children[0]))
            .toEqual(heroes[1].achievements.map(a => a.label));
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("deletes achievement", async () => {
        window.location.hash = "#test/heroes/" + heroes[0].id;
        const deleted = heroes[0].achievements[0].label;

        const app = renderer.create(<HeroControlPanel heroes={heroes} people={people} prefix ="#test" />);
        app.root.findByType(HeroAchievementList).findAllByProps({className: "deleteAchievementLink"})[0]
            .props.onClick({preventDefault: jest.fn});
        expect(heroes[0].achievements.map(a => a.label)).not.toContain(deleted);
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("shows achievement update", async () => {
        window.location.hash = "#test/heroes/" + heroes[1].id + "/achievement/" + heroes[1].achievements[1].id;

        const app = renderer.create(<HeroControlPanel heroes={heroes} people={people} prefix="#test" />);
        app.root.findByType(BoardMemberAchievementDetails);
        expect(app.toJSON()).toMatchSnapshot();
    });

    it("updates speaker achievement", async () => {
        window.location.hash = "#test/heroes/" + heroes[1].id + "/achievement/" + heroes[1].achievements[0].id;

        const app = renderer.create(<HeroControlPanel heroes={heroes} people={people} prefix="#test" />);
        app.root.findByType(JavaBinSpeakerAchievementDetails);
        const achievementView = app.root.findByType(JavaBinSpeakerAchievementDetails);
        const [titleInput, dateInput] = achievementView.findAllByType("input");
        await titleInput.props.onChange({target: {value: "Updated title"}});
        await dateInput.props.onChange({target: {value: "2018/10/15"}});
        achievementView.findByType("button").props.onClick({preventDefault: jest.fn()});

        expect(heroes[1].achievements[0]).toHaveProperty("title", "Updated title");
        expect(app.toJSON()).toMatchSnapshot();
    });
});
