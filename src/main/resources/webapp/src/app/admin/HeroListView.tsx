import { List, ListItem, ListItemIcon, ListItemText } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import StarBorder from "@material-ui/icons/StarBorder";
import React from "react";
import { Hero } from "../../services/api";

export class HeroListView extends React.Component<{
  heroes: Hero[];
  prefix: string;
}> {
  render() {
    return (
      <>
        <h2>Heroes</h2>

        <List>
          {this.props.heroes.map(h => (
            <ListItem key={h.id} onClick={() => (window.location.hash = this.props.prefix + "/heroes/" + h.id)} button>
              <ListItemIcon>
                <StarBorder />
              </ListItemIcon>
              <ListItemText primary={h.name} secondary={h.email} />
            </ListItem>
          ))}
        </List>

        <Button href={this.props.prefix + "/heroes/add"} variant="contained" color="primary">
          Add hero
        </Button>
      </>
    );
  }
}
