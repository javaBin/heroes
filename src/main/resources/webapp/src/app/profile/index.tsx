import React from "react";
import { HeroProfile, HeroService } from "../../services";

interface ProfileState {
    loaded: boolean;
    profile: HeroProfile;
}

export class ProfileScreen extends React.Component<{heroService: HeroService}, ProfileState> {
    state = {
        loaded: false,
        profile: {
            consent: { id: 0, text: "" },
            heroism: { achievement: "" },
            profile: { name: "", email: "" },
        },
    };

    componentDidMount = async () => {
        const profile = await this.props.heroService.fetchMe();
        this.setState({profile, loaded: true});
    }

    handlePublishSubmit = async () => {
        await this.props.heroService.consentToPublish(this.state.profile.consent.id);
        const profile = await this.props.heroService.fetchMe();
        this.setState({profile, loaded: true});
    }

    render() {
        if (!this.state.loaded) {
            return <div>Loading...</div>;
        }
        const {profile} = this.state;
        if (!profile.heroism) {
            return <div>You are not a hero, {profile.profile.name}</div>;
        }
        return <>
            <h1>{profile.profile.name}</h1>
            <div>{profile.heroism.achievement}</div>
            {profile.consent && <>
                <h2>We need your consent to publish the information about you</h2>
                {profile.consent.text}
                <button onClick={this.handlePublishSubmit}>I agree to be published</button>
            </>}
        </>;
    }
}
