import {
  Avatar,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Dialog,
  DialogActions,
  DialogTitle,
  IconButton,
  List,
  ListItem,
  ListItemSecondaryAction,
  ListItemText,
  TextField,
  Typography
} from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";
import DeleteIcon from "@material-ui/icons/Delete";
import FolderIcon from "@material-ui/icons/Folder";
import React, { FormEvent, MouseEvent } from "react";
import { Hero, HeroAchievement, HeroAchievementDetail } from "../../services/api";
import { AddHeroAchievementView } from "./achievements/AddHeroAchievementView";
import { EditHeroAchievementView } from "./achievements/EditHeroAchievementView";

export interface HeroEditProps {
  heroId: string;
  action?: string;
  actionTargetId?: string;
  prefix: string;
  onLoadHero: (id: string) => Promise<Hero>;
  onSubmit: (id: string, hero: Partial<Hero>) => void;
  onAddAchievement: (heroId: string, achievement: HeroAchievementDetail) => void;
  onUpdateAchievement: (heroId: string, achievementId: string, achievement: HeroAchievementDetail) => void;
  onDeleteAchievement: (heroId: string, achievementId: string) => void;
}

export class HeroView extends React.Component<HeroEditProps, { hero?: Hero }> {
  constructor(props: HeroEditProps) {
    super(props);
    this.state = {};
  }
  async componentDidMount() {
    this.refresh();
  }
  async refresh() {
    const hero = await this.props.onLoadHero(this.props.heroId);
    this.setState({ hero });
    document.title = "Admin | " + hero.name + " | javaBin heroes";
  }
  render() {
    const { action, prefix, onDeleteAchievement } = this.props;
    const { hero } = this.state;
    if (!hero) {
      return null;
    }

    if (!action) {
      return <HeroCard hero={hero} prefix={prefix} onDeleteAchievement={onDeleteAchievement} />;
    } else if (action === "edit") {
      return <HeroEditView hero={hero} onSubmit={this.props.onSubmit} prefix={prefix} />;
    } else if (action === "addAchievement") {
      return <AddHeroAchievementView hero={hero} onSubmit={this.props.onAddAchievement} prefix={prefix} />;
    } else if (action === "achievement" && this.props.actionTargetId) {
      return (
        <EditHeroAchievementView
          hero={hero}
          achievementId={this.props.actionTargetId}
          onSubmit={this.props.onUpdateAchievement}
          prefix={prefix}
        />
      );
    } else {
      throw new Error("Unknown action " + action);
    }
  }
}

export class HeroAchievementList extends React.Component<
  {
    hero: Hero;
    achievements: HeroAchievementDetail[];
    prefix: string;
    onDeleteAchievement: (heroId: string, achievementId: string) => void;
  },
  {
    deletingId?: string;
  }
> {
  constructor(props: {
    hero: Hero;
    achievements: HeroAchievementDetail[];
    prefix: string;
    onDeleteAchievement: (heroId: string, achievementId: string) => void;
  }) {
    super(props);
    this.state = {};
  }

  handleDeleteAchievement = (e: MouseEvent, achievementId: string) => {
    e.preventDefault();
    this.props.onDeleteAchievement(this.props.hero.id!, achievementId);
  };

  renderAchievement = (a: HeroAchievement) => {
    const { prefix, hero } = this.props;
    const target = prefix + "/heroes/" + hero.id + "/achievement/" + a.id;
    return (
      <ListItem
        key={a.id}
        button
        href={target}
        onClick={() => {
          window.location.hash = target;
        }}
      >
        <Avatar>
          <FolderIcon />
        </Avatar>
        <ListItemText primary={a.label} />
        <ListItemSecondaryAction>
          <IconButton onClick={() => this.setState({ deletingId: a.id })}>
            <DeleteIcon />
          </IconButton>
        </ListItemSecondaryAction>
      </ListItem>
    );
  };

  render() {
    const { deletingId } = this.state;
    return (
      <CardContent>
        <Typography gutterBottom variant="h5" component="h2">
          Achievements
        </Typography>
        <List>{this.props.achievements.map(this.renderAchievement)}</List>
        <Dialog open={!!deletingId}>
          <DialogTitle>Delete?</DialogTitle>
          <DialogActions>
            <Button
              color="primary"
              onClick={e => this.handleDeleteAchievement(e, deletingId!)}
              className="deleteAchievementLink"
            >
              Yes
            </Button>
            <Button color="primary" onClick={() => this.setState({ deletingId: undefined })}>
              No
            </Button>
          </DialogActions>
        </Dialog>
      </CardContent>
    );
  }
}

export function HeroCard({
  hero,
  prefix,
  onDeleteAchievement
}: {
  hero: Hero;
  prefix: string;
  onDeleteAchievement(heroId: string, id: string): void;
}) {
  document.title = hero.name + " | javaBin heroes";
  const { achievements } = hero;
  return (
    <Card>
      <CardHeader
        title={hero.name}
        subheader={hero.email + " " + hero.twitter}
        avatar={<Avatar src={hero.avatar_image}>{hero.name[0]}</Avatar>}
        action={
          <IconButton href={prefix}>
            <CloseIcon />
          </IconButton>
        }
      />
      {achievements && (
        <HeroAchievementList
          achievements={achievements}
          prefix={prefix}
          hero={hero}
          onDeleteAchievement={onDeleteAchievement}
        />
      )}
      <CardActions>
        <Button size="small" color="primary" href={prefix + "/heroes/" + hero.id + "/edit"}>
          Edit
        </Button>
        >
        <Button size="small" color="primary" href={prefix + "/heroes/" + hero.id + "/addAchievement"}>
          Add achievement
        </Button>
      </CardActions>
    </Card>
  );
}

class HeroEditView extends React.Component<
  { prefix: string; hero: Hero; onSubmit: (id: string, hero: Partial<Hero>) => void },
  Partial<Hero>
> {
  constructor(props: { prefix: string; hero: Hero; onSubmit: (id: string, hero: Partial<Hero>) => void }) {
    super(props);
    const { name, email, twitter } = props.hero;
    this.state = { name, email, twitter };
    document.title = "Edit | " + name + " | javaBin heroes";
  }

  handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const { email, name, twitter } = this.state;
    const hero = { email, name, twitter };
    await this.props.onSubmit(this.props.hero.id!, hero);
  };

  render() {
    const { prefix, hero } = this.props;
    const { name, email, twitter } = this.state;
    return (
      <>
        <h2>{hero.name} (edit)</h2>
        <form onSubmit={this.handleSubmit}>
          {hero.avatar_image && <Avatar src={hero.avatar_image} alt={"Picture of " + hero.name} />}
          <div>
            <TextField
              autoFocus
              label="Display name"
              required
              value={name}
              onChange={e => this.setState({ name: e.target.value })}
            />
          </div>
          <div>
            <TextField
              autoFocus
              label="Email address"
              required
              type="email"
              value={email}
              onChange={e => this.setState({ email: e.target.value })}
            />
          </div>
          <div>
            <TextField
              autoFocus
              label="Twitter handle"
              value={twitter}
              type=""
              onChange={e => this.setState({ twitter: e.target.value })}
            />
          </div>
          <div>
            <Button type="submit" color="primary">
              Lagre
            </Button>
          </div>
        </form>
        <Button href={prefix + "/heroes/" + hero.id}>Back</Button>
      </>
    );
  }
}
