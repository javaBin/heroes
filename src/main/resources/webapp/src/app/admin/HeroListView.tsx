import { List, ListItemText } from "@material-ui/core";
import Button from "@material-ui/core/Button";
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

        <List>
          {this.props.heroes.map(h => (
            <ListItem>
              <ListItemText primary={h.name} secondary={h.email} />
            </ListItem>
          ))}
        </List>

        <ul>{this.props.heroes.map(this.renderHero)}</ul>

        <Button href={this.props.prefix + "/heroes/add"} variant="contained" color="primary">
          Hero
        </Button>
      </>
    );
  }
}
