import React, { ChangeEvent, FormEvent, MouseEvent } from "react";
import { Hero, HeroService, Person } from "../../services/api";

export class AddHeroView extends React.Component<
  {
    adminService: HeroService;
    onSubmit: (hero: Hero) => void;
    onCancel: () => void;
  },
  Partial<Person> & {
    people: Person[];
  } & {
    loading: boolean;
  }
> {
  constructor(props: { adminService: HeroService; onSubmit: (hero: Hero) => void; onCancel: () => void }) {
    super(props);
    this.state = { loading: true, people: [] };
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
    const { name, email, twitter } = this.state;
    if (email && name) {
      this.props.onSubmit({ name, email, twitter, achievements: [], published: false });
    }
  };
  render() {
    if (this.state.loading || !this.state.people) {
      return <div>Please wait...</div>;
    }
    return (
      <form onSubmit={this.handleSubmit}>
        <h2>Add a hero</h2>

        <div>
          <label>
            Select from slack:
            <select onChange={this.handleSelectSlackPerson} value={this.state.email} autoFocus>
              <option />
              {this.state.people.map(p => (
                <option key={p.email} value={p.email}>
                  {p.name} &lt;{p.email}&gt;
                </option>
              ))}
            </select>
          </label>
        </div>
        <div>
          <label>
            Name:
            <input value={this.state.name} onChange={e => this.setState({ name: e.target.value })} />
          </label>
        </div>
        <div>
          <label>
            Email:
            <input value={this.state.email} onChange={e => this.setState({ email: e.target.value })} />
          </label>
        </div>
        <div>
          <label>
            Twitter:
            <input value={this.state.twitter} onChange={e => this.setState({ twitter: e.target.value })} />
          </label>
        </div>
        <button>Submit</button>
        <button onClick={this.handleCancel}>Cancel</button>
      </form>
    );
  }
}
