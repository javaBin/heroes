import { Button, TextField } from "@material-ui/core";
import React, { ChangeEvent } from "react";
import { Achievement, achievementName, allAchievements, Hero } from "../../../services/api";
import { achievementDetail } from "./achievementDetail";

export class AddHeroAchievementView extends React.Component<
  {
    hero: Hero;
    prefix: string;
    onSubmit(heroId: string, o: any): void;
  },
  {
    achievementType?: Achievement;
    achievementTypeString?: string;
  }
> {
  constructor(props: {
    hero: Hero;
    prefix: string;
    onSubmit: (heroId: string, o: any) => void;
    achievementTypes: Achievement[];
  }) {
    super(props);
    this.state = {};
    document.title = "Add achivement | " + props.hero.name + " | javaBin heroes";
  }
  renderAchievementType = (achivementType: Achievement) => {
    return (
      <option key={achivementType} value={achivementType}>
        {achievementName(achivementType)}
      </option>
    );
  };
  handleSave = (o: any) => {
    const { achievementType } = this.state;
    this.setState({ achievementType: undefined });
    this.props.onSubmit(this.props.hero.id!, { type: achievementType, ...o });
  };
  handleChangeAchievementType = (e: ChangeEvent<HTMLSelectElement>) => {
    const { value } = e.target;
    const achievementType: Achievement = value as Achievement;
    this.setState({ achievementType, achievementTypeString: value });
  };
  render() {
    const { achievementTypeString } = this.state;
    const { hero } = this.props;
    const DetailComponent = achievementDetail(this.state.achievementType);
    return (
      <>
        <h3>Add achievement</h3>
        <form>
          <TextField
            label="Achievement"
            autoFocus
            select
            value={achievementTypeString}
            onChange={this.handleChangeAchievementType}
            SelectProps={{
              native: true
            }}
          >
            <option />
            {allAchievements().map(this.renderAchievementType)}
          </TextField>
          <DetailComponent hero={hero} onSave={this.handleSave} />
        </form>
        <Button href={this.props.prefix + "/heroes/" + hero.id}>Back</Button>
      </>
    );
  }
}
