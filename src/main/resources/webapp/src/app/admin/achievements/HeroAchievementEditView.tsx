import React from "react";
import { Hero, HeroAchievementDetail } from "../../../services/api";
import { achievementDetail } from "./achievementDetail";

interface HeroAchievementEditProps {
  hero: Hero;
  achievementId: string;
  prefix: string;
  onSubmit: (heroId: string, achievementId: string, o: HeroAchievementDetail) => void;
}

export class HeroAchievementEditView extends React.Component<HeroAchievementEditProps> {
  handleSave = (update: HeroAchievementDetail) => {
    this.props.onSubmit(this.props.hero.id!, this.props.achievementId, update);
  };
  render() {
    const { hero, achievementId } = this.props;
    const achievement = hero.achievements.find(a => a.id === achievementId);
    if (!achievement) {
      return null;
    }
    const DetailView = achievementDetail(achievement.type);
    return (
      <form>
        <DetailView hero={hero} onSave={this.handleSave} achievement={achievement} />
      </form>
    );
  }
}
