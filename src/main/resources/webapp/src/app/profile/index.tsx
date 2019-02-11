import {
  Card,
  CardContent,
  CardHeader,
  List,
  ListItem,
  ListItemText,
  Paper,
  Typography,
  WithStyles,
  withStyles
} from "@material-ui/core";
import React from "react";
import { HeroProfile, HeroService } from "../../services";
import { styles } from "../styles";

interface ProfileState {
  loaded: boolean;
  profile: HeroProfile;
}

export const ProfileScreen = withStyles(styles)(
  class extends React.Component<{ heroService: HeroService } & WithStyles<typeof styles>, ProfileState> {
    constructor(props: { heroService: HeroService } & WithStyles<typeof styles>) {
      super(props);
      this.state = {
        loaded: false,
        profile: {
          consent: { id: 0, text: "" },
          achievements: [],
          profile: { name: "", email: "" }
        }
      };
    }

    componentDidMount = async () => {
      document.title = "javaBin heroes";
      const profile = await this.props.heroService.fetchMe();
      this.setState({ profile, loaded: true });
      document.title = profile.profile.name + " | javaBin heroes";
    };

    handlePublishSubmit = async () => {
      await this.props.heroService.consentToPublish(this.state.profile.consent!.id);
      const profile = await this.props.heroService.fetchMe();
      this.setState({ profile, loaded: true });
    };

    render() {
      if (!this.state.loaded) {
        return <div>Loading...</div>;
      }
      const { profile } = this.state;
      if (!profile.achievements.length) {
        return <div>You are not a hero, {profile.profile.name}</div>;
      }
      return (
        <Paper className={this.props.classes.paper}>
          <Typography variant="h4">{profile.profile.name}</Typography>
          <Typography>We have recorded the following about you:</Typography>
          <List>
            <ListItem>
              <ListItemText primary={profile.profile.name} secondary="Name" />
            </ListItem>
            <ListItem>
              <ListItemText primary={profile.profile.email} secondary="Email" />
            </ListItem>
            <ListItem>
              <ListItemText primary={profile.profile.twitter} secondary="Twitter" />
            </ListItem>
          </List>
          <Card>
            <CardHeader title="Your achievements:" />
            <CardContent>
              <List>
                {profile.achievements.map(achievement => (
                  <ListItem key={achievement.id}>
                    <ListItemText primary={achievement.label} />
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </Card>
          {profile.consent && (
            <>
              <Typography variant="h4">We need your consent to publish the information about you</Typography>
              {profile.consent.text}
              <button onClick={this.handlePublishSubmit}>I agree to be published</button>
            </>
          )}
        </Paper>
      );
    }
  }
);
