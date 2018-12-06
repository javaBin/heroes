import React from "react";
import { HeroService } from "../../services";
import { CreateHeroData, Hero } from "../../services/heroService";

interface AdminProps {
  heroService: HeroService;
}

interface AdminState {
  createHeroData?: CreateHeroData;
}

export class AdminScreen extends React.Component<AdminProps, AdminState> {
  state: AdminState;

  constructor(props: AdminProps) {
    super(props);
    this.state = {};
  }

  async componentDidMount() {
    const createHeroData = await this.props.heroService.fetchCreateHeroData();
    this.setState({ createHeroData });
  }

  handleNewHero = async (hero: Hero) => {
    await this.props.heroService.addHero(hero);
    window.location.hash = "";
  }

  render() {
    const {createHeroData} = this.state;
    if (!createHeroData) {
      return <div>Loading...</div>;
    }
    return <>
      <div className="heroes-admin-container">
        <h1>javaBin Heroes</h1>
        <NewHeroForm onNewHero={this.handleNewHero} createHeroData={createHeroData} />
      </div>
    </>;

  }
}

function FormControl({label, type, value, onChange}:
  {label: string, type?: string, value: string, onChange: (s: string) => void},
  ) {
  return <label>
    {label}
    <input type={type} value={value} onChange={event => onChange(event.target.value)} />
  </label>;
}

function DateControl({label}: {label: string}) {
  return <label>
    {label}
    <input type="date" />
  </label>;
}

interface SelectOption { value: string; label: string; }

function SelectControl({label, options, value, onChange}:
  {label: string, options: SelectOption[], value: string, onChange: (s: string) => void},
  ) {
  return <label>
    {label}
    <select value={value} onChange={event => onChange(event.target.value)}>
      {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
    </select>
  </label>;
}

class NewHeroForm extends React.Component<{onNewHero: (h: Hero) => void, createHeroData: CreateHeroData}> {
    state = {
      achievement: "",
      email: "",
      name: "",
    };

    handleSubmit = () => {
      const {email, name, achievement} = this.state;
      const newHero = {
        achievement, email, name,
        published: false,
      };
      this.props.onNewHero(newHero);
    }

    render = () => {

      const {name, email, achievement} = this.state;

      const people = this.props.createHeroData.people.map(p => ({label: p.name, value: p.email}));
      const achievements = this.props.createHeroData.achievements;

      return (
        <div className="heroes-admin-add">
          <form onSubmit={this.handleSubmit}>
            <SelectControl label="Person" options={people} value={email} onChange={(email) => this.setState({email})} />
            <FormControl label="Hero name" value={name} onChange={(name) => this.setState({name})} />
            <FormControl label="Hero email" type="email" value={email} onChange={(email) => this.setState({email})} />
            <SelectControl
              label="Hero type"
              options={achievements}
              value={achievement}
              onChange={(achievement) => this.setState({achievement})}
            />
            <DateControl label="Dato" />
            <button>Legg til</button>
          </form>
        </div>
      );
    }
}
