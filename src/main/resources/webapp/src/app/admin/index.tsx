import React from "react";
import { Hero, HeroAchievementDetail, HeroService } from "../../services/api";
import { AddHeroView } from "./AddHeroView";
import { HeroListView } from "./HeroListView";
import { HeroView } from "./HeroView";

export class HeroControlPanel extends React.Component<
  {
    heroService: HeroService;
    prefix: string;
  },
  {
    loading: boolean;
    addHero?: boolean;
    action?: string;
    idOrAction?: string;
    heroes: Hero[];
    actionTargetId?: string;
  }
> {
  constructor(props: { heroService: HeroService; prefix: string }) {
    super(props);
    this.state = { heroes: [], loading: true };
  }

  async componentDidMount() {
    window.addEventListener("hashchange", this.handleHashChange);
    this.setHash(window.location.hash);

    const heroes = await this.props.heroService.fetchHeroes();
    this.setState({ heroes, loading: false });
  }

  componentWillUnmount() {
    window.removeEventListener("hashchange", this.handleHashChange);
  }

  handleHashChange = (event: HashChangeEvent) => {
    this.setHash(window.location.hash);
  };

  setHash = async (hash: string) => {
    const [prefix, controller, idOrAction, subAction, actionTargetId] = hash.split("/");
    if (prefix && prefix !== this.props.prefix) {
      // tslint:disable-next-line:no-console
      console.warn("Unexpected URL", prefix, "should be", this.props.prefix);
    }
    if (idOrAction === "add") {
      await this.setState({ addHero: true });
    } else if (controller === "heroes" && idOrAction) {
      await this.setState({ addHero: false, action: subAction, idOrAction, actionTargetId });
    } else {
      await this.setState({ addHero: false, action: undefined, idOrAction: undefined, actionTargetId: undefined });
    }
  };

  handleLoadHero = async (id: string) => {
    const hero = await this.props.heroService.fetchHeroDetails(id);
    return hero;
  };

  handleAddHero = async (hero: Hero) => {
    this.setState({ loading: true });
    await this.props.heroService.addHero(hero);
    const heroes = await this.props.heroService.fetchHeroes();
    this.setState({ heroes, loading: false });
    window.location.hash = this.props.prefix + "/heroes";
  };

  handleUpdateHero = async (heroId: string, update: Partial<Hero>) => {
    this.setState({ loading: true });
    await this.props.heroService.updateHero(heroId, update);
    const heroes = await this.props.heroService.fetchHeroes();
    this.setState({ heroes, loading: false });
    window.location.hash = this.props.prefix + "/heroes/" + heroId;
  };

  handleAddAchievement = async (heroId: string, achievement: HeroAchievementDetail) => {
    this.setState({ loading: true });
    await this.props.heroService.addAchievement(heroId, achievement);
    const heroes = await this.props.heroService.fetchHeroes();
    this.setState({ heroes, loading: false });
    window.location.hash = this.props.prefix + "/heroes/" + heroId;
  };

  handleUpdateAchievement = async (heroId: string, achievementId: string, achievement: HeroAchievementDetail) => {
    await this.props.heroService.updateAchievement(heroId, achievementId, achievement);
    const heroes = await this.props.heroService.fetchHeroes();
    this.setState({ heroes, loading: false });
    window.location.hash = this.props.prefix + "/heroes/" + heroId;
  };

  handleDeleteAchievement = async (heroId: string, achievementId: string) => {
    this.setState({ loading: true });
    await this.props.heroService.deleteAchievement(heroId, achievementId);
    const heroes = await this.props.heroService.fetchHeroes();
    this.setState({ heroes, loading: false });
    window.location.hash = this.props.prefix + "/heroes/" + heroId;
  };

  handleCancelAddHero = () => {
    window.location.hash = this.props.prefix + "/heroes";
  };

  render() {
    if (this.state.loading) {
      return <div>Please wait...</div>;
    }
    if (this.state.addHero) {
      return (
        <AddHeroView
          adminService={this.props.heroService}
          onSubmit={this.handleAddHero}
          onCancel={this.handleCancelAddHero}
        />
      );
    }
    const { heroes, idOrAction } = this.state;
    const selectedHero = heroes.find(p => p.id === idOrAction);

    if (!selectedHero || !selectedHero.id) {
      return <HeroListView heroes={heroes} prefix={this.props.prefix} />;
    } else {
      return (
        <HeroView
          heroId={selectedHero.id}
          action={this.state.action}
          actionTargetId={this.state.actionTargetId}
          prefix={this.props.prefix}
          onLoadHero={this.handleLoadHero}
          onSubmit={this.handleUpdateHero}
          onAddAchievement={this.handleAddAchievement}
          onUpdateAchievement={this.handleUpdateAchievement}
          onDeleteAchievement={this.handleDeleteAchievement}
        />
      );
    }
  }
}
