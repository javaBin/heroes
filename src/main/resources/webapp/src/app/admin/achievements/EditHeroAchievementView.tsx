import { Button } from "@material-ui/core";
import React from "react";
import { Hero, HeroAchievementDetail } from "../../../services/api";
import { achievementDetail } from "./achievementDetail";

interface HeroAchievementEditProps {
  hero: Hero;
  achievementId: string;
  prefix: string;
  onSubmit: (heroId: string, achievementId: string, o: HeroAchievementDetail) => void;
}

export class EditHeroAchievementView extends React.Component<HeroAchievementEditProps> {
  constructor(props: HeroAchievementEditProps) {
    super(props);
    document.title = "Update achievement | " + props.hero.name + " | javaBin heroes";
  }

  handleSave = (update: HeroAchievementDetail) => {
    this.props.onSubmit(this.props.hero.id!, this.props.achievementId, update);
  };
  render() {
    const { hero, achievementId, prefix } = this.props;
    const achievement = hero.achievements.find(a => a.id === achievementId);
    if (!achievement) {
      return null;
    }
    const DetailView = achievementDetail(achievement.type);
    return (
      <>
        <h3>Update achievement</h3>
        <form>
          <DetailView hero={hero} onSave={this.handleSave} achievement={achievement} />
          <Button href={prefix + "/heroes/" + hero.id}>Back</Button>
        </form>
      </>
    );
  }
}
