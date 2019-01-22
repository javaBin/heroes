import { Button, TextField } from "@material-ui/core";
import React, { FormEvent } from "react";
import { UsergroupSpeakerAchievement } from "../../../services/api";
import { HeroAchievementProps } from "./HeroAchievementProps";

export class JavaBinSpeakerAchievementDetails extends React.Component<
  HeroAchievementProps & {
    achievement?: Partial<UsergroupSpeakerAchievement>;
  },
  {
    date: string;
    title: string;
  }
> {
  constructor(
    props: HeroAchievementProps & {
      achievement?: {
        date?: string;
        title?: string;
      };
    }
  ) {
    super(props);
    this.state = {
      title: (props.achievement && props.achievement.title) || "",
      date: (props.achievement && props.achievement.date) || new Date().toDateString()
    };
  }
  handleSubmit = (e: FormEvent) => {
    const { date, title } = this.state;
    this.props.onSave({ type: "FOREDRAGSHOLDER_JAVABIN", date, title });
    e.preventDefault();
  };
  render() {
    return (
      <>
        <div>
          <TextField label="Title" value={this.state.title} onChange={e => this.setState({ title: e.target.value })} />
        </div>
        <div>
          <TextField
            label="Date"
            type="date"
            value={this.state.date}
            onChange={e => this.setState({ date: e.target.value })}
          />
        </div>
        <Button onClick={this.handleSubmit} disabled={!this.state.title.length || !this.state.date}>
          Submit
        </Button>
      </>
    );
  }
}
