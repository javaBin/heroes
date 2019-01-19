import React, { FormEvent } from "react";
import { BoardMemberAchivement, BoardMemberRole, boardMemberRoleName } from "../../../services/api";
import { HeroAchievementProps } from "./HeroAchievementProps";

export class BoardMemberAchievementDetails extends React.Component<
  HeroAchievementProps & {
    achievement?: Partial<BoardMemberAchivement>;
  },
  {
    year?: string;
    role: BoardMemberRole;
  }
> {
  years: string[];
  roles: BoardMemberRole[];
  constructor(
    props: HeroAchievementProps & {
      achievement?: {
        year?: string;
        role?: string;
      };
    }
  ) {
    super(props);

    this.years = ["2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008"];
    this.roles = ["BOARD_MEMBER", "VICE_CHAIR", "CHAIR"];
    this.state = { role: this.roles[0], year: this.years[0], ...props.achievement };
  }
  handleSubmit = (e: FormEvent) => {
    const { year, role } = this.state;
    this.props.onSave({ type: "STYRE", year: parseInt(year!, 10), role });
    e.preventDefault();
  };
  render() {
    return (
      <>
        <h3>JavaBin board member</h3>
        <label>
          Role
          <select value={this.state.role} onChange={e => this.setState({ role: e.target.value as BoardMemberRole })}>
            {this.roles.map(r => (
              <option value={r} key={r}>
                {boardMemberRoleName(r)}
              </option>
            ))}
          </select>
        </label>
        <label>
          Elected year
          <select value={this.state.year} onChange={e => this.setState({ year: e.target.value })}>
            {this.years.map(y => (
              <option value={y} key={y}>
                {y}
              </option>
            ))}
          </select>
        </label>
        <button onClick={this.handleSubmit} disabled={!this.state.year || !this.state.role.length}>
          Submit
        </button>
      </>
    );
  }
}
