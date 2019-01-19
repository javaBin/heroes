import React, { FormEvent } from "react";
import { ConferenceSpeakerAchievement } from "../../../services/api";
import { HeroAchievementProps } from "./HeroAchievementProps";

export class JavaZoneSpeakerAchievementDetails extends React.Component<
  HeroAchievementProps & {
    achievement?: ConferenceSpeakerAchievement;
  },
  {
    year: number;
    title: string;
  }
> {
  years: number[];
  constructor(
    props: HeroAchievementProps & {
      achievement?: {
        year: number;
        title: string;
      };
    }
  ) {
    super(props);
    this.years = [2018, 2017, 2016, 2015, 2014, 2013, 2012, 2011, 2010, 2009, 2008];
    this.state = { year: this.years[0], title: "", ...props.achievement };
  }
  handleSubmit = (e: FormEvent) => {
    const { year, title } = this.state;
    this.props.onSave({ type: "FOREDRAGSHOLDER_JZ", year, title });
    e.preventDefault();
  };
  render() {
    return (
      <div>
        <h4>JavaZone foredragsholder</h4>
        <label>
          JavaZone year
          <select
            value={this.state.year.toString()}
            onChange={e => this.setState({ year: parseInt(e.target.value.toString(), 10) })}
            autoFocus
          >
            {this.years.map(y => (
              <option value={y} key={y}>
                {y}
              </option>
            ))}
          </select>
        </label>
        <label>
          Title:
          <input value={this.state.title} onChange={e => this.setState({ title: e.target.value })} />
        </label>
        <button onClick={this.handleSubmit} disabled={!this.state.title.length}>
          Submit
        </button>
      </div>
    );
  }
}
