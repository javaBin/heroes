import {
  Avatar,
  Button,
  FormControl,
  InputLabel,
  Paper,
  Select,
  TextField,
  Typography,
  withStyles,
  WithStyles
} from "@material-ui/core";
import React, { ChangeEvent, FormEvent, MouseEvent } from "react";
import { Hero, HeroService, Person } from "../../services/api";
import { styles } from "../styles";

interface AddHeroProps extends WithStyles<typeof styles> {
  adminService: HeroService;
  onSubmit: (hero: Hero) => void;
  onCancel: () => void;
}

export const AddHeroView = withStyles(styles)(
  class extends React.Component<
    AddHeroProps,
    Partial<Person> & {
      people: Person[];
    } & {
      loading: boolean;
    }
  > {
    constructor(props: AddHeroProps) {
      super(props);
      this.state = { loading: true, people: [], name: "", email: "", twitter: "" };
      document.title = "Add hero | javaBin heroes";
    }
    async componentDidMount() {
      const { people } = await this.props.adminService.fetchCreateHeroData();
      this.setState({ people, loading: false });
    }
    handleSelectSlackPerson = (e: ChangeEvent<HTMLSelectElement>) => {
      const { value } = e.target;
      const person = this.state.people.find(p => p.email === value);
      if (person) {
        this.setState({ ...person });
      }
    };
    handleCancel = (e: MouseEvent) => {
      e.preventDefault();
      this.props.onCancel();
    };
    handleSubmit = (e: FormEvent) => {
      e.preventDefault();
      const { name, email, twitter, avatar_image } = this.state;
      if (email && name) {
        this.props.onSubmit({ name, email, twitter, avatar_image, achievements: [], published: false });
      }
    };
    render() {
      if (this.state.loading || !this.state.people) {
        return <div>Please wait...</div>;
      }
      return (
        <Paper className={this.props.classes.paper}>
          <form onSubmit={this.handleSubmit}>
            <Typography variant="h4">Add a hero</Typography>

            <div>
              <FormControl>
                <InputLabel htmlFor="add-hero-slack-member">Select from slack:</InputLabel>
                <Select
                  onChange={this.handleSelectSlackPerson}
                  value={this.state.email}
                  autoFocus
                  inputProps={{ id: "add-hero-slack-member" }}
                  native
                >
                  <option />
                  {this.state.people.map(p => (
                    <option key={p.email} value={p.email}>
                      {p.name} &lt;{p.email}&gt;
                    </option>
                  ))}
                </Select>
              </FormControl>
            </div>
            {this.state.avatar_image && (
              <div>
                <Avatar src={this.state.avatar_image} />
              </div>
            )}
            <div>
              <TextField label="Name" value={this.state.name} onChange={e => this.setState({ name: e.target.value })} />
              <TextField
                label="Email"
                value={this.state.email}
                onChange={e => this.setState({ email: e.target.value })}
              />
              <TextField
                label="Twitter"
                value={this.state.twitter}
                onChange={e => this.setState({ twitter: e.target.value })}
              />
            </div>
            <div>
              <Button type="submit" variant="contained" color="primary">
                Submit
              </Button>
              <Button onClick={this.handleCancel} variant="contained">
                Cancel
              </Button>
            </div>
          </form>
        </Paper>
      );
    }
  }
);
