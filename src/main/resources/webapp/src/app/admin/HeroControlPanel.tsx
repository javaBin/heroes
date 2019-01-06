import React, { ChangeEvent, FormEvent, MouseEvent } from "react";
import {
    Achievement, achievementName, allAchievements, Hero, HeroAchievement, HeroService, Person,
} from "../../services/api";

export class HeroControlPanel extends React.Component<{
    heroService: HeroService, prefix: string,
}, {
    loading: boolean,
    addHero?: boolean, action?: string,
    idOrAction?: string,
    heroes: Hero[], actionTargetId?: string,
}> {

    constructor(props: {heroService: HeroService, prefix: string}) {
        super(props);
        this.state = {heroes: [], loading: true };
    }

    async componentDidMount() {
        window.addEventListener("hashchange", this.handleHashChange);
        this.setHash(window.location.hash);

        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loading: false});
    }

    componentWillUnmount() {
        window.removeEventListener("hashchange", this.handleHashChange);
    }

    handleHashChange = (event: HashChangeEvent) => {
        this.setHash(window.location.hash);
    }

    setHash = async (hash: string) => {
        const [prefix, controller, idOrAction, subAction, actionTargetId] = hash.split("/");
        if (idOrAction === "add") {
            await this.setState({addHero: true});
        } else if (controller === "heroes" && idOrAction) {
            await this.setState({addHero: false, action: subAction, idOrAction, actionTargetId});
        } else {
            await this.setState({addHero: false, action: undefined, idOrAction: undefined, actionTargetId: undefined});
        }
    }

    handleLoadHero = async (id: string) => {
        const hero = await this.props.heroService.fetchHeroDetails(id);
        return hero;
    }

    handleAddHero = async (hero: Hero) => {
        this.setState({loading: true});
        await this.props.heroService.addHero(hero);
        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loading: false});
        window.location.hash = this.props.prefix + "/heroes";
    }

    handleUpdateHero = async (heroId: string, update: Partial<Hero>) => {
        this.setState({loading: true});
        await this.props.heroService.updateHero(heroId, update);
        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loading: false});
        window.location.hash = this.props.prefix + "/heroes/" + heroId;
    }

    handleAddAchievement = async (heroId: string, achievement: HeroAchievement) => {
        this.setState({loading: true});
        await this.props.heroService.addAchievement(heroId, achievement);
        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loading: false});
        window.location.hash = this.props.prefix + "/heroes/" + heroId;
    }

    handleUpdateAchievement = async (heroId: string, achievementId: string, achievement: any) => {
        await this.props.heroService.updateAchievement(heroId, achievementId, achievement);
        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loading: false});
        window.location.hash = this.props.prefix + "/heroes/" + heroId;
    }

    handleDeleteAchievement = async (heroId: string, achievementId: string) => {
        this.setState({loading: true});
        await this.props.heroService.deleteAchievement(heroId, achievementId);
        const heroes = await this.props.heroService.fetchHeroes();
        this.setState({heroes, loading: false});
        window.location.hash = this.props.prefix + "/heroes/" + heroId;
    }

    handleCancelAddHero = () => {
        window.location.hash = this.props.prefix + "/heroes";
    }

    render() {
        if (this.state.loading) {
            return <div>Please wait...</div>;
        }
        if (this.state.addHero) {
            return <AddHeroView
                adminService={this.props.heroService}
                onSubmit={this.handleAddHero}
                onCancel={this.handleCancelAddHero}
            />;
        }
        const {heroes, idOrAction} = this.state;
        const selectedHero = heroes.find(p => p.id === idOrAction);

        if (!selectedHero) {
            return <HeroListView heroes={heroes} prefix={this.props.prefix} />;
        } else {
            return <HeroView
                hero={selectedHero}
                action={this.state.action}
                actionTargetId={this.state.actionTargetId}
                prefix={this.props.prefix}
                onLoadHero={this.handleLoadHero}
                onSubmit={this.handleUpdateHero}
                onAddAchievement={this.handleAddAchievement}
                onUpdateAchievement={this.handleUpdateAchievement}
                onDeleteAchievement={this.handleDeleteAchievement}
            />;
        }
    }
}

export class AddHeroView extends React.Component<{
    adminService: HeroService,
    onSubmit: (hero: Hero) => void,
    onCancel: () => void,
}, Partial<Person> & {people: Person[]} & {loading: boolean}> {
    constructor(props: {
        adminService: HeroService, onSubmit: (hero: Hero) => void, onCancel: () => void,
    }) {
        super(props);
        this.state = { loading: true, people: [] };
    }

    async componentDidMount() {
        const {people} = await this.props.adminService.fetchCreateHeroData();
        this.setState({people, loading: false});
    }

    handleSelectSlackPerson = (e: ChangeEvent<HTMLSelectElement>) => {
        const {value} = e.target;
        const person = this.state.people.find(p => p.email === value);
        if (person) {
            this.setState({...person});
        }
    }

    handleCancel = (e: MouseEvent) => {
        e.preventDefault();
        this.props.onCancel();
    }

    handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        const {name, email, twitter} = this.state;
        if (email && name) {
            this.props.onSubmit({name, email, twitter, achievements: [], published: false});
        }
    }

    render() {
        if (this.state.loading || !this.state.people) {
            return <div>Please wait...</div>;
        }
        return <form onSubmit={this.handleSubmit}>
            <h2>Add a hero</h2>

            <div>
                <label>
                    Select from slack:
                    <select onChange={this.handleSelectSlackPerson} value={this.state.email} autoFocus>
                        <option></option>
                        {this.state.people.map(p =>
                            <option key={p.email} value={p.email}>{p.name} &lt;{p.email}&gt;</option>,
                        )}
                    </select>
                </label>
            </div>
            <div>
                <label>
                    Name:
                    <input value={this.state.name} onChange={e => this.setState({name: e.target.value})} />
                </label>
            </div>
            <div>
                <label>
                    Email:
                    <input value={this.state.email} onChange={e => this.setState({email: e.target.value})} />
                </label>
            </div>
            <div>
                <label>
                    Twitter:
                    <input value={this.state.twitter} onChange={e => this.setState({twitter: e.target.value})} />
                </label>
            </div>
            <button>Submit</button>
            <button onClick={this.handleCancel}>Cancel</button>
        </form>;
    }
}

interface HeroEditProps {
    hero: Hero;
    action?: string;
    actionTargetId?: string;
    prefix: string;
    onLoadHero: (id: string) => Promise<Hero>;
    onSubmit: (id: string, hero: Partial<Hero>) => void;
    onAddAchievement: (heroId: string, achievement: any) => void;
    onUpdateAchievement: (heroId: string, achievementId: string, achievement: any) => void;

    onDeleteAchievement: (heroId: string, achievementId: string) => void;
}

export class HeroAchievementList extends React.Component<{
    hero: Hero, achievements: any[], prefix: string,
    onDeleteAchievement: (heroId: string, achievementId: string) => void,
}> {

    handleDeleteAchievement = (e: MouseEvent, achievementId: string) => {
        e.preventDefault();
        this.props.onDeleteAchievement(this.props.hero.id!, achievementId);
    }

    renderAchievement = (achievement: HeroAchievement) => {
        return <li key={achievement.label}>
            {achievement.label} [
                <a href={this.props.prefix + "/heroes/" + this.props.hero.id + "/achievement/" + achievement.id}>
                Edit
                </a>]
                [
                <a
                    href="#"
                    onClick={e => this.handleDeleteAchievement(e, achievement.id!)}
                    className="deleteAchievementLink"
                >
                Delete
                </a>]
        </li>;
    }

    render() {
        return <>
            <h3>Achievements</h3>

            <ul>
                {this.props.achievements.map(this.renderAchievement)}
            </ul>
        </>;
    }
}

export class HeroView extends React.Component<HeroEditProps, Partial<Hero> & {hero: Hero}> {
    constructor(props: HeroEditProps) {
        super(props);
        const {hero} = props;
        this.state = {
            achievements: hero.achievements,
            email: hero.email,
            hero,
            name: hero.name,
            published: hero.published,
            twitter: hero.twitter,
        };
    }

    async componentDidMount() {
        const hero = await this.props.onLoadHero(this.props.hero.id!);
        this.setState({...hero, hero});
    }

    handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        const {email, name, twitter} = this.state;
        const hero = {email, name, twitter};
        this.props.onSubmit(this.props.hero.id!, hero);
    }

    render() {
        const {prefix, onDeleteAchievement} = this.props;
        const {hero, name, email, twitter, achievements} = this.state;
        if (!hero) {
            return null;
        }
        return <>
            <h3><a href={prefix}>Back</a></h3>
            <h2>{hero.name}</h2>

            {!this.props.action && <>
                <ul>
                    <li>Email: {hero.email}</li>
                    <li>Twitter: {hero.twitter}</li>
                    <li><a href={prefix + "/heroes/" + hero.id + "/edit"}>Update</a></li>
                </ul>

                {achievements && <HeroAchievementList
                    hero={hero}
                    achievements={achievements}
                    prefix={prefix}
                    onDeleteAchievement={onDeleteAchievement}
                />}

                <p>
                    <a href={prefix + "/heroes/" + hero.id + "/addAchievement"}>Add achievement</a>
                </p>
            </>}

            {this.props.action === "edit" && <>
                <form onSubmit={this.handleSubmit}>
                {hero.avatar && <img src={hero.avatar} alt={"Picture of " + hero.name} />}
                    <h3>Information</h3>
                    <ul>
                        <li>Display name:
                            <input
                                autoFocus
                                value={name}
                                onChange={e => this.setState({name: e.target.value})}
                            />
                        </li>
                        <li>Email address:
                            <input value={email} onChange={e => this.setState({email: e.target.value})} />
                        </li>
                        <li>Twitter handle:
                            <input
                                value={twitter}
                                onChange={e => this.setState({twitter: e.target.value})}
                            />
                        </li>
                    </ul>

                    <button>Lagre</button>
                    <a href={prefix + "/heroes/" + hero.id}>Back</a>
                </form>
            </>}

            {this.props.action === "addAchievement" &&
                <AddHeroAchievement
                    hero={hero}
                    onSubmit={this.props.onAddAchievement}
                    prefix={prefix}
                />
            }
            {this.props.action === "achievement" && this.props.actionTargetId !== undefined &&
                <HeroAchievementEditView
                    hero={hero}
                    achievementId={this.props.actionTargetId}
                    onSubmit={this.props.onUpdateAchievement}
                    prefix={prefix}
                />
            }
        </>;
    }
}

interface HeroAchievementEditProps {
    hero: Hero;
    achievementId: string;
    prefix: string ;
    onSubmit: (heroId: string, achievementId: string, o: any) => void;
}

export class HeroAchievementEditView extends React.Component<HeroAchievementEditProps> {
    handleSave = (update: any) => {
        this.props.onSubmit(this.props.hero.id!, this.props.achievementId, update);
    }

    render() {
        const {hero, achievementId} = this.props;
        const achievement = hero.achievements.find(a => a.id === achievementId);
        if (!achievement) {
            return null;
        }
        const DetailView = achievementDetail(achievement.type);
        return <form>
            <DetailView hero={hero} onSave={this.handleSave} achievement={achievement} />
        </form>;
    }
}

interface HeroAchievementProps {
    hero: Hero;
    achievement?: any;
    onSave(o: any): void;
}

export class JavaZoneSpeakerAchievementDetails extends React.Component<
    HeroAchievementProps & {achievement?: {year?: string, title?: string}}, {
    year?: string, title: string,
}> {
    years: string[];
    constructor(props: HeroAchievementProps & {achievement?: {year?: string, title?: string}}) {
        super(props);
        this.years = [
            "2018", "2017", "2016", "2015", "2014",
            "2013", "2012", "2011", "2010", "2009",
            "2008",
        ];
        this.state = { year: this.years[0], title: "", ...props.achievement };
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
                <select value={this.state.year} onChange={e => this.setState({year: e.target.value})} autoFocus>
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

export class JavaBinSpeakerAchievementDetails extends React.Component<
    HeroAchievementProps & {achievement?: {date?: string, title?: string}}, {
    date?: string,
    title: string,
}> {
    constructor(props: HeroAchievementProps & {achievement?: {date?: string, title?: string}}) {
        super(props);
        this.state = {title: "", ...props.achievement};
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

export class BoardMemberAchievementDetails extends React.Component<
    HeroAchievementProps & {achievement?: {year?: string, role?: string}}, {
    year?: string, role: string,
}> {
    years: string[];
    roles: string[];
    constructor(props: HeroAchievementProps & {achievement?: {year?: string, role?: string}}) {
        super(props);
        this.years = [
            "2018", "2017", "2016", "2015", "2014",
            "2013", "2012", "2011", "2010", "2009",
            "2008",
        ];
        this.roles = [
            "board member", "vice chair", "chair",
        ];
        this.state = { role: this.roles[0], year: this.years[0], ...props.achievement };
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

function achievementDetail(achievementType?: Achievement): React.ComponentType<HeroAchievementProps> {
    switch (achievementType) {
    case "foredragsholder_javabin":   return JavaBinSpeakerAchievementDetails;
    case "foredragsholder_jz":        return JavaZoneSpeakerAchievementDetails;
    case "styre":                     return BoardMemberAchievementDetails;
    case undefined:                   return EmptyAchievementDetails;
    }
}

export class AddHeroAchievement extends React.Component<{
    hero: Hero, prefix: string, onSubmit(heroId: string, o: any): void,
}, {
    achievementType?: Achievement, achievementTypeString?: string,
}> {
    constructor(props: {
        hero: Hero, prefix: string, onSubmit: (heroId: string, o: any) => void, achievementTypes: Achievement[],
    }) {
        super(props);
        this.state = {};
    }

    renderAchievementType = (achivementType: Achievement) => {
        return <option
            key={achivementType}
            value={achivementType}
        >{achievementName(achivementType)}</option>;
    }

    handleSave = (o: any) => {
        const {achievementType} = this.state;
        this.setState({achievementType: undefined});
        this.props.onSubmit(this.props.hero.id!, {type: achievementType, ...o});
    }

    handleChangeAchievementType = (e: ChangeEvent<HTMLSelectElement>) => {
        const {value} = e.target;
        const achievementType: Achievement = value as Achievement;
        this.setState({achievementType, achievementTypeString: value});
    }

    render() {
        const {achievementTypeString} = this.state;
        const {hero} = this.props;

        const DetailComponent = achievementDetail(this.state.achievementType);

        return <>
            <h3>Please Add achievement</h3>
            <form>
                <label>
                    Achievement:
                    <select autoFocus={true} value={achievementTypeString} onChange={this.handleChangeAchievementType}>
                        <option></option>
                        {allAchievements().map(this.renderAchievementType)}
                    </select>
                </label>
                <DetailComponent hero={hero} onSave={this.handleSave} ></DetailComponent>
                <a href={this.props.prefix + "/heroes/" + hero.id}>Back</a>
            </form>
        </>;
    }
}

export class HeroListView extends React.Component<{heroes: Hero[], prefix: string}> {

    renderHero = (hero: Hero) => {
        return <li key={hero.id}><a href={this.props.prefix + "/heroes/" + hero.id}>{hero.name}</a></li>;
    }

    render() {
        return <>
            <h2>Here are all the heroes</h2>
            <ul>
                {this.props.heroes.map(this.renderHero)}
            </ul>

            <a href={this.props.prefix + "/heroes/add"}>Add a hero</a>
        </>;
    }
}
