import React from "react";
import renderer, { ReactTestRenderer } from "react-test-renderer";
import { Hero, HeroAchievementDetail, HeroService, Person } from "../src/services/api";
import { MockHeroService } from "../src/services/mockHeroService";

import { CardHeader, IconButton, ListItemSecondaryAction, ListItemText, TextField } from "@material-ui/core";
import { HeroControlPanel } from "../src/app/admin";
import { AddHeroAchievementView } from "../src/app/admin/achievements/AddHeroAchievementView";
import { BoardMemberAchievementDetails } from "../src/app/admin/achievements/BoardMemberAchievementDetails";
import { JavaBinSpeakerAchievementDetails } from "../src/app/admin/achievements/JavaBinSpeakerAchievementDetails";
import { JavaZoneSpeakerAchievementDetails } from "../src/app/admin/achievements/JavaZoneSpeakerAchievementDetails";
import { AddHeroView } from "../src/app/admin/AddHeroView";
import { HeroListView } from "../src/app/admin/HeroListView";
import { HeroAchievementList } from "../src/app/admin/HeroView";

const fakeHeroService: HeroService = new MockHeroService();

jest.mock("../src/services/heroServiceHttp", () => ({
  HeroServiceHttp: () => fakeHeroService
}));

// tslint:disable-next-line:no-eval
eval(`global["window"] = {
    location: {
        hash: ""
    },
    addEventListener() {},
    removeEventListener() {}
}`);
// tslint:disable-next-line:no-eval
eval(`global["document"] = {}`);

function promiseCompletion() {
  return new Promise(resolve => setImmediate(resolve));
}

describe("HeroControlPanel", () => {
  let heroes: Hero[] = [];

  const people: Person[] = [
    { email: "johannes@example.com", name: "Johannes Brodwall" },
    { email: "alice@example.com", name: "Alice" },
    { email: "bob@example.net", name: "Bob" }
  ];

  beforeEach(() => {
    heroes = [
      {
        achievements: [
          // tslint:disable-next-line:max-line-length
          { id: "13", label: "ABC", type: "FOREDRAGSHOLDER_JAVABIN", date: new Date("2017-12-01"), title: "A" },
          // tslint:disable-next-line:max-line-length
          { id: "14", label: "XYZ", type: "FOREDRAGSHOLDER_JAVABIN", date: new Date("2017-12-01"), title: "A" }
        ],
        email: "johannes@example.com",
        id: "1",
        name: "Johannes Test",
        published: false
      },
      {
        achievements: [
          // tslint:disable-next-line:max-line-length
          {
            id: "11",
            label: "A great girl",
            type: "FOREDRAGSHOLDER_JAVABIN",
            date: new Date("2017/9/13"),
            title: "ABC"
          },
          { id: "12", label: "A super guy!", type: "STYRE", role: "BOARD_MEMBER", year: 2019 }
        ],
        email: "test@example.com",
        id: "2",
        name: "Some test",
        published: true
      }
    ];
    fakeHeroService.addHero = async hero => {
      hero.id = "" + (heroes.length + 1);
      heroes.push(hero);
    };
    fakeHeroService.fetchCreateHeroData = () => Promise.resolve({ people });
    fakeHeroService.fetchHeroes = () => Promise.resolve(heroes);
    fakeHeroService.updateHero = async (heroId, update) => {
      const index = heroes.findIndex(h => h.id === heroId)!;
      heroes[index] = { ...heroes[index], ...update };
    };
    fakeHeroService.addAchievement = (heroId: string, achievement: HeroAchievementDetail) => {
      heroes.find(h => h.id === heroId)!.achievements.push(achievement);
      return Promise.resolve();
    };
    fakeHeroService.updateAchievement = async (heroId, achievementId, update) => {
      const hero = heroes.find(h => h.id === heroId)!;
      const achievementIndex = hero.achievements.findIndex(a => a.id === achievementId);
      hero.achievements[achievementIndex] = { ...hero.achievements[achievementIndex], ...update };
    };
    fakeHeroService.deleteAchievement = async (heroId, achievementId) => {
      const hero = heroes.find(h => h.id === heroId)!;
      const achievementIndex = hero.achievements.findIndex(a => a.id === achievementId);
      hero.achievements.splice(achievementIndex);
    };
  });

  it("fakes hero service", async () => {
    const { people } = await fakeHeroService.fetchCreateHeroData();
    expect(people.length).toBe(3);
  });

  it("shows list of current heroes", async () => {
    // tslint:disable-next-line:max-line-length
    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    expect(app.toJSON()).toMatchSnapshot();
    expect(
      app.root
        .findByType(HeroListView)
        .findAllByType(ListItemText)
        .map(li => li.props.primary)
    ).toEqual(["Johannes Test", "Some test"]);
  });

  it("shows hero status", async () => {
    window.location.hash = "#test/heroes/" + heroes[1].id;

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    expect(app.root.findByType(CardHeader).props.title).toBe(heroes[1].name);
    expect(app.toJSON()).toMatchSnapshot();
  });

  it("shows new hero screen", async () => {
    window.location.hash = "#test/heroes/add";

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    expect(app.root.findByType(AddHeroView).findByType("h2").children[0]).toBe("Add a hero");
    expect(app.toJSON()).toMatchSnapshot();
  });

  it("adds hero from slack list", async () => {
    window.location.hash = "#test/heroes/add";

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();

    app.root.findByType("select").props.onChange({ target: { value: people[1].email } });
    app.root
      .findByType(AddHeroView)
      .findByType("form")
      .props.onSubmit({ preventDefault: jest.fn() });
    await promiseCompletion();

    expect(app.root.instance.state.heroes.map((h: Hero) => h.name)).toContain(people[1].name);
  });

  describe("adds achievement to hero", async () => {
    let app: ReactTestRenderer;
    beforeEach(async () => {
      window.location.hash = "#test/heroes/" + heroes[1].id + "/addAchievement";

      app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
      await promiseCompletion();
    });

    describe("of JavaZone achievement type", () => {
      beforeEach(async () => {
        expect(app.root.findAll(c => true).map(c => c.type)).toContain(AddHeroAchievementView);
        app.root.findByType(AddHeroAchievementView).instance.setState({ achievementType: "FOREDRAGSHOLDER_JZ" });
        await promiseCompletion();
      });

      it("shows achievement form", async () => {
        app.root.findByType(JavaZoneSpeakerAchievementDetails);
        expect(app.toJSON()).toMatchSnapshot();
      });

      it("creates new achievement", async () => {
        const form = app.root.findByType(AddHeroAchievementView).findByType("form");
        const titleInput = form.findByType("input");
        const submitButton = form.findByType("button");
        titleInput.props.onChange({ target: { value: "My Talk" } });
        submitButton.props.onClick({ preventDefault: jest.fn() });
        await promiseCompletion();

        expect(app.root.instance.state.heroes[1].achievements.map((a: any) => a.title)).toContain("My Talk");
      });
    });

    it("shows JavaBin achievement type", async () => {
      app.root.findByType(AddHeroAchievementView).instance.setState({ achievementType: "FOREDRAGSHOLDER_JAVABIN" });
      await promiseCompletion();
      app.root.findByType(JavaBinSpeakerAchievementDetails);
      expect(app.toJSON()).toMatchSnapshot();
    });

    it("shows board member achievement type", async () => {
      app.root.findByType(AddHeroAchievementView).instance.setState({ achievementType: "STYRE" });
      await promiseCompletion();
      app.root.findByType(BoardMemberAchievementDetails);
      expect(app.toJSON()).toMatchSnapshot();
    });
  });

  it("shows hero edit view", async () => {
    window.location.hash = "#test/heroes/" + heroes[1].id + "/edit";

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    const [displayName, email, twitter] = app.root.findByType("form").findAllByType("input");
    expect(displayName.props.value).toEqual(heroes[1].name);
    expect(email.props.value).toEqual(heroes[1].email);
    expect(twitter.props.value).toEqual(heroes[1].twitter);

    expect(app.toJSON()).toMatchSnapshot();
  });

  it("udpates hero name", async () => {
    window.location.hash = "#test/heroes/" + heroes[0].id + "/edit";

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    const [displayName] = app.root.findByType("form").findAllByType("input");
    await displayName.props.onChange({ target: { value: "Updated name" } });
    app.root.findByType("form").props.onSubmit({ preventDefault: jest.fn() });

    await promiseCompletion();
    expect(heroes[0].name).toEqual("Updated name");

    expect(app.toJSON()).toMatchSnapshot();
  });

  xit("lists achievements", async () => {
    window.location.hash = "#test/heroes/" + heroes[1].id;

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);

    await promiseCompletion();
    expect(
      app.root
        .findByType(HeroAchievementList)
        .findAllByType("li")
        .map(li => li.children[0])
    ).toEqual(heroes[1].achievements.map(a => a.label));
    expect(app.toJSON()).toMatchSnapshot();
  });

  xit("deletes achievement", async () => {
    window.location.hash = "#test/heroes/" + heroes[0].id;
    const deleted = heroes[0].achievements[0].label;

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    app.root
      .findByType(HeroAchievementList)
      .findAllByType(ListItemSecondaryAction)[0]
      .findByType(IconButton)
      .props.onClick({ preventDefault: jest.fn });
    app.root
      .findByType(HeroAchievementList)
      .findByProps({ className: "deleteAchievementLink" })
      .props.onClick({ preventDefault: jest.fn });
    await promiseCompletion();
    expect(heroes[0].achievements.map(a => a.label)).not.toContain(deleted);
    expect(app.toJSON()).toMatchSnapshot();
  });

  it("shows achievement update", async () => {
    window.location.hash = "#test/heroes/" + heroes[1].id + "/achievement/" + heroes[1].achievements[1].id;

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    app.root.findByType(BoardMemberAchievementDetails);
    expect(app.toJSON()).toMatchSnapshot();
  });

  it("updates speaker achievement", async () => {
    window.location.hash = "#test/heroes/" + heroes[1].id + "/achievement/" + heroes[1].achievements[0].id;

    const app = renderer.create(<HeroControlPanel heroService={fakeHeroService} prefix="#test" />);
    await promiseCompletion();
    app.root.findByType(JavaBinSpeakerAchievementDetails);
    const achievementView = app.root.findByType(JavaBinSpeakerAchievementDetails);
    const [titleInput, dateInput] = achievementView.findAllByType(TextField);
    await titleInput.props.onChange({ target: { value: "Updated title" } });
    await dateInput.props.onChange({ target: { value: "2018/10/15" } });
    achievementView.findByType("button").props.onClick({ preventDefault: jest.fn() });

    expect(heroes[1].achievements[0]).toHaveProperty("title", "Updated title");
    expect(app.toJSON()).toMatchSnapshot();
  });
});
