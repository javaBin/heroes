import React from "react";
import { Hero } from "../../services/api";

export class HeroListView extends React.Component<{
  heroes: Hero[];
  prefix: string;
}> {
  renderHero = (hero: Hero) => {
    return (
      <li key={hero.id}>
        <a href={this.props.prefix + "/heroes/" + hero.id}>{hero.name}</a>
      </li>
    );
  };
  render() {
    return (
      <>
        <h2>Here are all the heroes</h2>
        <ul>{this.props.heroes.map(this.renderHero)}</ul>

        <a href={this.props.prefix + "/heroes/add"}>Add a hero</a>
      </>
    );
  }
}
