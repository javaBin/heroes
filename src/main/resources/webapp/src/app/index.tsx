import React from "react";
import { HeroService } from "../services";
import { Hero } from "../services/heroService";
import { AdminScreen } from "./admin";

import { Table } from "react-bootstrap";
import { ProfileScreen } from "./profile";

import heroPng from "../images/hero.png";

export function HeroList({heroes}: {heroes: Hero[]}) {
    const heroComponents = heroes.map((hero) => {
        return (
          <tr>
            <td>
              <img height="25" src={heroPng} />
            </td>
            <td>
              {hero.name}
            </td>
            <td>
              {hero.contribution}
            </td>
          </tr>
        );
    });
    return (
        <div className="heroes-list">
          <h2>Helter</h2>
          <Table striped bordered condensed hover>
            <thead>
              <tr>
                <th></th>
                <th>Navn</th>
                <th>Heltetype</th>
              </tr>
            </thead>
            <tbody>
              {heroComponents}
            </tbody>
          </Table>
        </div>
      );
}

export class HeroListComponent extends React.Component<{heroService: HeroService}, {heroes?: Hero[], loaded: boolean}> {
    state = {
        heroes: undefined,
        loaded: false,
    };

    componentDidMount = async () => {
        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loaded: true});
    }

    render() {
        if (!this.state.loaded) {
            return <div>Loading....</div>;
        }
        return <HeroList heroes={this.state.heroes!} />;
    }
}

export class App extends React.Component<{heroService: HeroService}, {hash: string}> {

    state = {
        hash: window.location!.hash,
    };

    handleHashchange = () => {
        const {hash} = window.location;
        this.setState({hash});
    }

    async componentDidMount() {
        window.addEventListener("hashchange", this.handleHashchange);
    }

    componentWillUnmount() {
        window.removeEventListener("hashchange", this.handleHashchange);
    }

    renderContent() {
        const { hash } = this.state;
        if (hash.indexOf("#admin") === 0) {
            return <AdminScreen heroService={this.props.heroService} />;
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
        return <>
            <nav>
                <ul>
                    <li><a href="#">View heroes</a></li>
                    <li><a href="#admin">Admin</a></li>
                    <li><a href="#profile">View my profile</a></li>
                </ul>
            </nav>
            {this.renderContent()}
        </>;
    }

}
