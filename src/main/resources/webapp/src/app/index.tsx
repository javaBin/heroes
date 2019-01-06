import React from "react";
import { HeroService } from "../services";
import { Hero, Userinfo } from "../services/api";
import { AdminScreen } from "./admin";

import { Table } from "react-bootstrap";
import { ProfileScreen } from "./profile";

import heroPng from "../images/hero.png";
import { HeroServiceHttp } from "../services/heroServiceHttp";
import { HeroControlPanel } from "./admin/HeroControlPanel";

export function HeroList({heroes}: {heroes: Hero[]}) {
    const heroComponents = heroes.map((hero) => {
        return (
          <tr key={hero.email}>
            <td>
              <img height="25" src={heroPng} />
            </td>
            <td>
              {hero.name}
            </td>
            <td>
              {hero.achievement}
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

export class App extends React.Component<{heroService: HeroService}, {hash: string, userinfo: Userinfo}> {

    state = {
        hash: window.location!.hash,
        userinfo: { authenticated: false, username: "", admin: false },
    };

    handleHashchange = () => {
        const {hash} = window.location;
        this.setState({hash});
    }

    async componentDidMount() {
        window.addEventListener("hashchange", this.handleHashchange);
        const userinfo = await this.props.heroService.fetchUserinfo();
        this.setState({userinfo});
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

        if (hash.indexOf("#poc") === 0) {
            return <HeroControlPanel heroService={new HeroServiceHttp()} prefix="#poc" />;
        }

        return (
            <div>
                <h1>{hash}</h1>
                <HeroListComponent heroService={this.props.heroService} />
            </div>
        );
    }

    render() {
        const {userinfo} = this.state;
        return <>
            <nav>
                <ul>
                    <li><a href="#">View heroes</a></li>
                    {userinfo.authenticated || <li><a href="/api/login">Log in</a></li>}
                    {userinfo.admin && <li><a href="#admin">Admin</a></li>}
                    {userinfo.authenticated && <li><a href="#profile">{userinfo.username}</a></li>}
                </ul>
            </nav>
            {this.renderContent()}
        </>;
    }

}
