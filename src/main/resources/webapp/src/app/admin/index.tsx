import React, { FormEvent } from "react";
import { HeroService } from "../../services";
import { Achievement, achievementName, allAchievements, CreateHeroData, Hero } from "../../services/heroService";

import heroPng from "../../images/hero.png";
import { HeroControlPanel } from "./HeroControlPanel";

interface AdminProps {
  heroService: HeroService;
}

interface AdminState {
  createHeroData?: CreateHeroData;
  heroes?: Hero[];
}

export class AdminScreen extends React.Component<AdminProps, AdminState> {
  state: AdminState;

  constructor(props: AdminProps) {
    super(props);
    this.state = {};
  }

  async componentDidMount() {
    const {heroService} = this.props;
    const [createHeroData, heroes] = [await heroService.fetchCreateHeroData(), await heroService.fetchHeroes()];
    this.setState({ createHeroData, heroes });
  }

  handleNewHero = async (hero: Hero) => {
    await this.props.heroService.addHero(hero);
    const {heroService} = this.props;
    const [createHeroData, heroes] = [await heroService.fetchCreateHeroData(), await heroService.fetchHeroes()];
    this.setState({ createHeroData, heroes });
  }

  render() {
    const {createHeroData, heroes} = this.state;
    if (!createHeroData || !heroes) {
      return <div>Loading...</div>;
    }
    return <>
      <div className="heroes-admin-container">
        <h1>javaBin Heroes</h1>
        <h2>Add Hero</h2>
        <NewHeroForm onNewHero={this.handleNewHero} createHeroData={createHeroData} />
        <h2>Current heroes</h2>
        <AdminHeroList heroes={heroes} />
      </div>
      <fieldset>
        <h2>UX Proof-of-concept heroes control panel:</h2>
        <HeroControlPanel heroes={heroes} people={createHeroData.people} prefix="#admin" />
      </fieldset>
    </>;

  }
}

function AdminHeroList({heroes}: {heroes: Hero[]}) {
  const heroComponents = heroes.map((hero) => {
    return (
      <tr key={hero.id}>
        <td>
          <img height="25" src={heroPng} />
        </td>
        <td>
          {hero.name}
        </td>
        <td>
          {hero.achievement}
        </td>
        <td>
          {hero.published ? "Published" : "Unpublished"}
        </td>
      </tr>
    );
});
  return (
    <div className="heroes-list">
      <h2>Helter</h2>
      <table>
        <thead>
          <tr>
            <th></th>
            <th>Navn</th>
            <th>Heltetype</th>
            <th>Published</th>
          </tr>
        </thead>
        <tbody>
          {heroComponents}
        </tbody>
      </table>
    </div>
  );
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

function SelectControl({label, options, value, onChange, includeBlank}:
  {label: string, options: SelectOption[], value: string, onChange: (s: string) => void, includeBlank: boolean},
  ) {
  return <label>
    {label}
    <select value={value} onChange={event => onChange(event.target.value)}>
      {includeBlank && <option></option>}
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

    handleSubmit = async (e: FormEvent) => {
      const {email, name, achievement} = this.state;
      const newHero = {
        achievement, achievements: [], avatar: "", email, name,
        published: false,
      };
      e.preventDefault();
      await this.props.onNewHero(newHero);
      this.setState({achievement: "", email: "", name: ""});
    }

    render = () => {

      const {name, email, achievement} = this.state;

      const people = this.props.createHeroData.people.map(p => ({label: p.name, value: p.email}));
      const achievements = allAchievements();

      return (
        <div className="heroes-admin-add">
          <form onSubmit={this.handleSubmit}>
            <SelectControl
              label="Person"
              options={people}
              value={email}
              onChange={(email) => this.setState({email})}
              includeBlank={true}
            />
            <FormControl label="Hero name" value={name} onChange={(name) => this.setState({name})} />
            <FormControl label="Hero email" type="email" value={email} onChange={(email) => this.setState({email})} />
            <SelectControl
              label="Hero type"
              options={achievements.map(a => ({value: Achievement[a], label: achievementName(a)}))}
              value={achievement}
              onChange={(achievement) => this.setState({achievement})}
              includeBlank={true}
            />
            <DateControl label="Dato" />
            <button>Legg til</button>
          </form>
        </div>
      );
    }
}
