import React, { ChangeEvent, FormEvent } from "react";
import { Achievement, achievementName, allAchievements, Hero, HeroAchievement } from "../../services/heroService";

export class HeroControlPanel extends React.Component<{
    heroes: Hero[],
}, {
    selectedHero?: Hero,
}> {

    constructor(props: {heroes: Hero[]}) {
        super(props);
        this.state = {};
    }

    componentDidMount() {
        window.addEventListener("hashchange", this.handleHashChange);
        this.setHash(window.location.hash);
    }

    componentWillUnmount() {
        window.removeEventListener("hashchange", this.handleHashChange);
    }

    handleHashChange = (event: HashChangeEvent) => {
        this.setHash(window.location.hash);
    }

    setHash = (hash: string) => {
        const match = /#admin\/heroes\/([\w-]+)/.exec(hash);
        if (match) {
            const heroId = match[1];
            this.setState({selectedHero: this.props.heroes.find(p => p.id === heroId)});
        } else {
            this.setState({selectedHero: undefined});
        }
    }

    render() {
        if (this.state.selectedHero) {
            return <HeroEditView hero={this.state.selectedHero} />;
        } else {
            return <HeroList heroes={this.props.heroes} />;
        }
    }
}

interface EditHeroState {
    displayName: string;
    emailAddress: string;
    twitterHandle: string;
    achievements: HeroAchievement[];
}

class HeroEditView extends React.Component<{hero: Hero}, EditHeroState> {
    constructor(props: {hero: Hero, achievementTypes: []}) {
        super(props);
        const {hero} = props;
        this.state = {
            achievements: hero.achievements,
            displayName: hero.name,
            emailAddress: hero.email,
            twitterHandle: "",
        };
    }

    renderAchievement = (achievement: HeroAchievement) => {
        return <li key={achievement.label}>{achievement.label}</li>;
    }

    handleAddAchievement = (o: any) => {
        this.setState({achievements: this.state.achievements.concat([o])});
    }

    render() {
        const {hero} = this.props;
        const {displayName, emailAddress, twitterHandle, achievements} = this.state;
        return <>
            <h3><a href="#admin">Back</a></h3>
            <h2>{hero.name}</h2>

            <form>
                {hero.avatar && <img src={hero.avatar} alt={"Picture of " + hero.name} />}
                <h2>Information</h2>
                <ul>
                    <li>Display name:
                        <input value={displayName} onChange={e => this.setState({displayName: e.target.value})} />
                    </li>
                    <li>Email address:
                        <input value={emailAddress} onChange={e => this.setState({emailAddress: e.target.value})} />
                    </li>
                    <li>Twitter handle:
                        <input value={twitterHandle} onChange={e => this.setState({twitterHandle: e.target.value})} />
                    </li>
                </ul>

                <button>Lagre</button>
            </form>

            <h2>Achievements</h2>

            <ul>
                {achievements.map(this.renderAchievement)}
            </ul>

            <AddHeroAchievement
                hero={hero}
                onAddAchievement={this.handleAddAchievement}
            />
        </>;
    }
}

interface HeroAchievementProps {
    hero: Hero;
    onSave(o: any): void;
}

class JavaZoneSpeakerAchievementDetails extends React.Component<HeroAchievementProps, {
    year?: string, title: string,
}> {
    years: string[];
    constructor(props: HeroAchievementProps) {
        super(props);
        this.years = [
            "2018", "2017", "2016", "2015", "2014",
            "2013", "2012", "2011", "2010", "2009",
            "2008",
        ];
        this.state = { year: this.years[0], title: "" };
    }

    handleSubmit = (e: FormEvent) => {
        const {year, title} = this.state;
        const label = "Speaker at JavaZone " + year + ": " + title;
        this.props.onSave({ year, title, label });
        e.preventDefault();
    }

    render() {
        return <div>
            <h4>JavaZone foredragsholder</h4>
            <label>
                JavaZone year
                <select value={this.state.year} onChange={e => this.setState({year: e.target.value})}>
                    {this.years.map(y => <option value={y} key={y}>{y}</option>)}
                </select>
            </label>
            <label>
                Title:
                <input value={this.state.title} onChange={e => this.setState({title: e.target.value})} />
            </label>
            <button onClick={this.handleSubmit} disabled={!this.state.title.length}>Submit</button>
        </div>;
    }
}

class JavaBinSpeakerAchievementDetails extends React.Component<HeroAchievementProps, {
    date?: string,
    title: string,
}> {
    constructor(props: HeroAchievementProps) {
        super(props);
        this.state = {title: ""};
    }
    handleSubmit = (e: FormEvent) => {
        const {date, title} = this.state;
        const label = `JavaBin speaker ${date}: ${title}`;
        this.props.onSave({ date, title, label });
        e.preventDefault();
    }

    render() {
        return <>
            <h3>JavaBin usergroup speaker</h3>
            <label>
                Title:
                <input value={this.state.title} onChange={e => this.setState({title: e.target.value})} />
            </label>
            <label>
                Date:
                <input type="date" value={this.state.date} onChange={e => this.setState({date: e.target.value})} />
            </label>
            <button onClick={this.handleSubmit} disabled={!this.state.title.length || !this.state.date}>Submit</button>
        </>;
    }
}

class BoardMemberAchievementDetails extends React.Component<HeroAchievementProps, {
    year?: string, role: string,
}> {
    years: string[];
    roles: string[];
    constructor(props: HeroAchievementProps) {
        super(props);
        this.years = [
            "2018", "2017", "2016", "2015", "2014",
            "2013", "2012", "2011", "2010", "2009",
            "2008",
        ];
        this.roles = [
            "board member", "vice chair", "chair",
        ];
        this.state = { role: this.roles[0], year: this.years[0] };
    }

    handleSubmit = (e: FormEvent) => {
        const {year, role} = this.state;
        const label = role + " i styret " + year;
        this.props.onSave({ year, role, label });
        e.preventDefault();
    }

    render() {
        return <>
            <h3>JavaBin board member</h3>
            <label>
                Role
                <select value={this.state.role} onChange={e => this.setState({role: e.target.value})}>
                    {this.roles.map(r => <option value={r} key={r}>{r}</option>)}
                </select>
            </label>
            <label>
                Elected year
                <select value={this.state.year} onChange={e => this.setState({year: e.target.value})}>
                    {this.years.map(y => <option value={y} key={y}>{y}</option>)}
                </select>
            </label>
            <button onClick={this.handleSubmit} disabled={!this.state.year || !this.state.role.length}>Submit</button>
        </>;
    }
}

class EmptyAchievementDetails extends React.Component<HeroAchievementProps> {
    render() {
        return <div></div>;
    }
}

class AddHeroAchievement extends React.Component<{
    hero: Hero, onAddAchievement(o: any): void,
}, {
    achievementType?: Achievement, achievementTypeString?: string,
}> {
    constructor(props: {hero: Hero, onAddAchievement: (o: any) => void, achievementTypes: Achievement[]}) {
        super(props);
        this.state = {};
    }

    achievementDetail(): React.ComponentType<HeroAchievementProps> {
        switch (this.state.achievementType) {
        case Achievement.foredragsholder_javabin:   return JavaBinSpeakerAchievementDetails;
        case Achievement.foredragsholder_jz:        return JavaZoneSpeakerAchievementDetails;
        case Achievement.styre:                     return BoardMemberAchievementDetails;
        case undefined:                             return EmptyAchievementDetails;
        }
    }

    renderAchievementType = (achivementType: Achievement) => {
        return <option
            key={achivementType}
            value={Achievement[achivementType]}
        >{achievementName(achivementType)}</option>;
    }

    handleSave = (o: any) => {
        this.props.onAddAchievement({type: this.state.achievementType, ...o});
        this.setState({achievementType: undefined});
    }

    handleChangeAchievementType = (e: ChangeEvent<HTMLSelectElement>) => {
        const {value} = e.target;
        const achievementType: Achievement = (Achievement as any)[value];
        this.setState({achievementType, achievementTypeString: value});
    }

    render() {
        const {achievementTypeString} = this.state;
        const {hero} = this.props;

        const DetailComponent = this.achievementDetail();

        return <>
            <h2>Please Add achievement</h2>
            <form>
                <label>
                    Achievement:
                    <select value={achievementTypeString} onChange={this.handleChangeAchievementType}>
                        <option></option>
                        {allAchievements().map(this.renderAchievementType)}
                    </select>
                </label>
                <DetailComponent hero={hero} onSave={this.handleSave} ></DetailComponent>
            </form>
        </>;
    }
}

export class HeroList extends React.Component<{heroes: Hero[]}> {

    renderHero = (hero: Hero) => {
        return <li key={hero.id}><a href={"#admin/heroes/" + hero.id}>{hero.name}</a></li>;
    }

    render() {
        return <>
            <h2>Here are all the heroes</h2>
            <ul>
                {this.props.heroes.map(this.renderHero)}
            </ul>
        </>;
    }
}
