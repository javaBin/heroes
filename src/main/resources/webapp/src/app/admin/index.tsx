import React from "react";
import { HeroService } from "../../services";
import { Hero } from "../../services/heroService";

export function AdminScreen({heroService}: {heroService: HeroService}) {
    async function handleNewHero(hero: Hero) {
      await heroService.addHero(hero);
      window.location.hash = "";
    }

    return <>
      <div className="heroes-admin-container">
        <h1>javaBin Heroes</h1>
        <NewHeroForm onNewHero={handleNewHero} />
      </div>
    </>;
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

function SelectControl({label, options}: {label: string, options: SelectOption[]}) {
  return <label>
    {label}
    <select>
      {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
    </select>
  </label>;
}

class NewHeroForm extends React.Component<{onNewHero: (h: Hero) => void}> {
    state = {
      email: "",
      name: "",
    };

    handleSubmit = () => {
      const {email, name} = this.state;
      const newHero = {
        email, name,
        published: false,
      };
      this.props.onNewHero(newHero);
    }

    render = () => {
      const heroTypes = [
        { value: "styremedlem", label: "Styremedlem" },
        { value: "foredragsholder-jz", label: "Foredragsholder på JavaZone" },
        { value: "foredragsholder", label: " Foredragsholder på javaBin" },
        { value: "regionsleder", label: "Regionsleder" },
        { value: "aktiv", label: "Aktiv" },
      ];

      const {name, email} = this.state;

      return (
        <div className="heroes-admin-add">
          <form onSubmit={this.handleSubmit}>
            <FormControl label="Hero name" value={name} onChange={(name) => this.setState({name})} />
            <FormControl label="Hero email" type="email" value={email} onChange={(email) => this.setState({email})} />
            <SelectControl label="Hero type" options={heroTypes} />
            <DateControl label="Dato" />
            <button>Legg til</button>
          </form>
        </div>
      );
    }
}
