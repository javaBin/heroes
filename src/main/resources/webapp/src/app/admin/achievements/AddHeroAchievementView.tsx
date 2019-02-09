import { Button, TextField, Typography } from "@material-ui/core";
import React, { ChangeEvent } from "react";
import { Achievement, achievementName, allAchievements, Hero } from "../../../services/api";
import { achievementDetail } from "./achievementDetail";

interface AddHeroAchievementProps {
  hero: Hero;
  prefix: string;
  onSubmit(heroId: string, o: any): void;
}

export class AddHeroAchievementView extends React.Component<
  AddHeroAchievementProps,
  {
    achievementType?: Achievement;
    achievementTypeString?: string;
  }
> {
  constructor(props: AddHeroAchievementProps) {
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
    const { achievementTypeString, achievementType } = this.state;
    const { handleChangeAchievementType, renderAchievementType, handleSave } = this;
    const { hero, prefix } = this.props;
    const DetailComponent = achievementDetail(achievementType);
    return (
      <>
        <Typography variant="h4">Add achievement</Typography>
        <form>
          <TextField
            label="Achievement"
            autoFocus
            select
            value={achievementTypeString}
            onChange={handleChangeAchievementType}
            SelectProps={{
              native: true
            }}
          >
            <option />
            {allAchievements().map(renderAchievementType)}
          </TextField>
          <DetailComponent hero={hero} onSave={handleSave} />
        </form>
        <Button href={prefix + "/heroes/" + hero.id}>Back</Button>
      </>
    );
  }
}
