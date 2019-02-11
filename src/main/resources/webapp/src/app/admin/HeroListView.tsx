import {
  Avatar,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Paper,
  Typography,
  WithStyles,
  withStyles
} from "@material-ui/core";
import Button from "@material-ui/core/Button";
import React from "react";
import { Hero } from "../../services/api";
import { styles } from "../styles";

export const HeroListView = withStyles(styles)(
  class extends React.Component<
    {
      heroes: Hero[];
      prefix: string;
    } & WithStyles<typeof styles>
  > {
    componentDidMount() {
      document.title = "Admin | javaBin heroes";
    }

    render() {
      return (
        <Paper className={this.props.classes.paper}>
          <Typography variant="h4">Heroes</Typography>

          <List>
            {this.props.heroes.map(h => (
              <ListItem
                key={h.id}
                onClick={() => (window.location.hash = this.props.prefix + "/heroes/" + h.id)}
                button
              >
                <ListItemIcon>
                  <Avatar src={h.avatarImage} />
                </ListItemIcon>
                <ListItemText primary={h.name} secondary={h.email} />
              </ListItem>
            ))}
          </List>

          <Button href={this.props.prefix + "/heroes/add"} variant="contained" color="primary">
            Add hero
          </Button>
        </Paper>
      );
    }
  }
);
