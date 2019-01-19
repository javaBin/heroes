import React, { FormEvent, MouseEvent } from "react";
import { Hero, HeroAchievement, HeroAchievementDetail } from "../../services/api";
import { AddHeroAchievement } from "./achievements/AddHeroAchievement";
import { HeroAchievementEditView } from "./achievements/HeroAchievementEditView";

export interface HeroEditProps {
  hero: Hero;
  action?: string;
  actionTargetId?: string;
  prefix: string;
  onLoadHero: (id: string) => Promise<Hero>;
  onSubmit: (id: string, hero: Partial<Hero>) => void;
  onAddAchievement: (heroId: string, achievement: HeroAchievementDetail) => void;
  onUpdateAchievement: (heroId: string, achievementId: string, achievement: HeroAchievementDetail) => void;
  onDeleteAchievement: (heroId: string, achievementId: string) => void;
}

export class HeroView extends React.Component<
  HeroEditProps,
  Partial<Hero> & {
    hero: Hero;
  }
> {
  constructor(props: HeroEditProps) {
    super(props);
    const { hero } = props;
    this.state = {
      achievements: hero.achievements,
      email: hero.email,
      hero,
      name: hero.name,
      published: hero.published,
      twitter: hero.twitter
    };
  }
  async componentDidMount() {
    this.refresh();
  }
  async refresh() {
    const hero = await this.props.onLoadHero(this.props.hero.id!);
    this.setState({ ...hero, hero });
  }
  handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const { email, name, twitter } = this.state;
    const hero = { email, name, twitter };
    await this.props.onSubmit(this.props.hero.id!, hero);
  };
  render() {
    const { prefix, onDeleteAchievement } = this.props;
    const { hero, name, email, twitter, achievements } = this.state;
    if (!hero) {
      return null;
    }
    return (
      <>
        <h3>
          <a href={prefix}>Back</a>
        </h3>
        <h2>{hero.name}</h2>

        {!this.props.action && (
          <>
            <ul>
              <li>Email: {hero.email}</li>
              <li>Twitter: {hero.twitter}</li>
              <li>
                <a href={prefix + "/heroes/" + hero.id + "/edit"}>Update</a>
              </li>
            </ul>

            {achievements && (
              <HeroAchievementList
                hero={hero}
                achievements={achievements}
                prefix={prefix}
                onDeleteAchievement={onDeleteAchievement}
              />
            )}

            <p>
              <a href={prefix + "/heroes/" + hero.id + "/addAchievement"}>Add achievement</a>
            </p>
          </>
        )}

        {this.props.action === "edit" && (
          <>
            <form onSubmit={this.handleSubmit}>
              {hero.avatar && <img src={hero.avatar} alt={"Picture of " + hero.name} />}
              <h3>Information</h3>
              <ul>
                <li>
                  Display name:
                  <input autoFocus value={name} onChange={e => this.setState({ name: e.target.value })} />
                </li>
                <li>
                  Email address:
                  <input value={email} onChange={e => this.setState({ email: e.target.value })} />
                </li>
                <li>
                  Twitter handle:
                  <input value={twitter} onChange={e => this.setState({ twitter: e.target.value })} />
                </li>
              </ul>

              <button>Lagre</button>
              <a href={prefix + "/heroes/" + hero.id}>Back</a>
            </form>
          </>
        )}

        {this.props.action === "addAchievement" && (
          <AddHeroAchievement hero={hero} onSubmit={this.props.onAddAchievement} prefix={prefix} />
        )}
        {this.props.action === "achievement" && this.props.actionTargetId !== undefined && (
          <HeroAchievementEditView
            hero={hero}
            achievementId={this.props.actionTargetId}
            onSubmit={this.props.onUpdateAchievement}
            prefix={prefix}
          />
        )}
      </>
    );
  }
}

export class HeroAchievementList extends React.Component<{
  hero: Hero;
  achievements: HeroAchievementDetail[];
  prefix: string;
  onDeleteAchievement: (heroId: string, achievementId: string) => void;
}> {
  handleDeleteAchievement = (e: MouseEvent, achievementId: string) => {
    e.preventDefault();
    this.props.onDeleteAchievement(this.props.hero.id!, achievementId);
  };

  renderAchievement = (achievement: HeroAchievement) => {
    return (
      <li key={achievement.label}>
        {achievement.label} [
        <a href={this.props.prefix + "/heroes/" + this.props.hero.id + "/achievement/" + achievement.id}>Edit</a>] [
        <a href="#" onClick={e => this.handleDeleteAchievement(e, achievement.id!)} className="deleteAchievementLink">
          Delete
        </a>
        ]
      </li>
    );
  };

  render() {
    return (
      <>
        <h3>Achievements</h3>

        <ul>{this.props.achievements.map(this.renderAchievement)}</ul>
      </>
    );
  }
}
