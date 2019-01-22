import {
  AppBar,
  Avatar,
  Button,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Toolbar,
  Typography
} from "@material-ui/core";
import AccountCircle from "@material-ui/icons/AccountCircle";
import MenuIcon from "@material-ui/icons/Menu";
import React from "react";
import { HeroService } from "../services";
import { Hero, Userinfo } from "../services/api";

import { ProfileScreen } from "./profile";

import { HeroServiceHttp } from "../services/heroServiceHttp";
import { HeroControlPanel } from "./admin";

export function HeroList({ heroes }: { heroes: Hero[] }) {
  return (
    <div className="heroes-list">
      <List>
        {heroes.map(h => (
          <ListItem key={h.id}>
            <ListItemAvatar>
              <Avatar src="/images/hero.png" />
            </ListItemAvatar>
            <ListItemText primary={h.name} secondary={h.achievements.map(a => a.label).join(", ")} />
          </ListItem>
        ))}
      </List>
    </div>
  );
}

export class HeroListComponent extends React.Component<
  { heroService: HeroService },
  { heroes?: Hero[]; loaded: boolean }
> {
  state = {
    heroes: undefined,
    loaded: false
  };

  componentDidMount = async () => {
    const heroes = await this.props.heroService.fetchPublicHeroes();
    this.setState({ heroes, loaded: true });
  };

  render() {
    if (!this.state.loaded) {
      return <div>Loading....</div>;
    }
    return <HeroList heroes={this.state.heroes!} />;
  }
}

export class App extends React.Component<{ heroService: HeroService }, { hash: string; userinfo: Userinfo }> {
  state = {
    hash: window.location!.hash,
    userinfo: { authenticated: false, username: "", admin: false }
  };

  handleHashchange = () => {
    const { hash } = window.location;
    this.setState({ hash });
  };

  async componentDidMount() {
    document.title = "javaBin heroes";
    window.addEventListener("hashchange", this.handleHashchange);
    const userinfo = await this.props.heroService.fetchUserinfo();
    this.setState({ userinfo });
  }

  componentWillUnmount() {
    window.removeEventListener("hashchange", this.handleHashchange);
  }

  renderContent() {
    const { hash } = this.state;
    if (hash.indexOf("#admin") === 0) {
      return <HeroControlPanel heroService={new HeroServiceHttp()} prefix="#admin" />;
    }

    if (hash.indexOf("#profile") === 0) {
      return <ProfileScreen heroService={this.props.heroService} />;
    }

    return (
      <div>
        <h1>{hash}</h1>
        <HeroListComponent heroService={this.props.heroService} />
      </div>
    );
  }

  render() {
    const { userinfo, hash } = this.state;
    const adminPage = hash.indexOf("#admin") === 0;
    return (
      <>
        <AppBar position="static" color={adminPage ? "secondary" : "primary"}>
          <Toolbar>
            <IconButton href="#" color="inherit">
              <MenuIcon />
            </IconButton>
            <Typography variant="h6" color="inherit" style={{ flexGrow: 1 }}>
              Heroes
            </Typography>
            {userinfo.admin ? (
              <Button href="#admin" color="inherit">
                Admin
              </Button>
            ) : (
              <></>
            )}
            {userinfo.authenticated ? (
              <IconButton href="#profile" color="inherit">
                <AccountCircle />
              </IconButton>
            ) : (
              <></>
            )}
            {!userinfo.authenticated ? (
              <IconButton href="/api/login" color="inherit">
                <AccountCircle />
              </IconButton>
            ) : (
              <></>
            )}
          </Toolbar>
        </AppBar>
        {this.renderContent()}
      </>
    );
  }
}
