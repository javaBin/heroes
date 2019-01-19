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
    this.state = { title: "", date: "", ...props.achievement };
  }
  handleSubmit = (e: FormEvent) => {
    const { date, title } = this.state;
    this.props.onSave({ type: "FOREDRAGSHOLDER_JAVABIN", date: new Date(date), title });
    e.preventDefault();
  };
  render() {
    return (
      <>
        <h3>JavaBin usergroup speaker</h3>
        <label>
          Title:
          <input value={this.state.title} onChange={e => this.setState({ title: e.target.value })} />
        </label>
        <label>
          Date:
          <input type="date" value={this.state.date} onChange={e => this.setState({ date: e.target.value })} />
        </label>
        <button onClick={this.handleSubmit} disabled={!this.state.title.length || !this.state.date}>
          Submit
        </button>
      </>
    );
  }
}
