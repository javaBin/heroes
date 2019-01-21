import { Button, TextField } from "@material-ui/core";
import React, { FormEvent } from "react";
import { ConferenceSpeakerAchievement } from "../../../services/api";
import { HeroAchievementProps } from "./HeroAchievementProps";

export class JavaZoneSpeakerAchievementDetails extends React.Component<
  HeroAchievementProps & {
    achievement?: ConferenceSpeakerAchievement;
  },
  {
    year: string;
    title: string;
  }
> {
  years: number[];
  constructor(
    props: HeroAchievementProps & {
      achievement?: {
        year: string;
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
    this.props.onSave({ type: "FOREDRAGSHOLDER_JZ", year: parseInt(year, 10), title });
    e.preventDefault();
  };
  render() {
    const { year } = this.state;
    return (
      <>
        <div>
          <TextField
            required
            select
            SelectProps={{ native: true }}
            label="JavaZone year"
            value={year}
            onChange={e => this.setState({ year: e.target.value })}
          >
            {this.years.map(y => (
              <option value={y} key={y}>
                {y}
              </option>
            ))}
          </TextField>
        </div>
        <div>
          <TextField
            label="Talk title"
            value={this.state.title}
            onChange={e => this.setState({ title: e.target.value })}
          />
        </div>
        <Button type="submit" onClick={this.handleSubmit} disabled={!this.state.title.length} color="primary">
          Submit
        </Button>
      </>
    );
  }
}
