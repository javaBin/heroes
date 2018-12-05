import React from "react";
import { HeroService } from "../../services";
import { Hero } from "../../services/heroService";

interface ProfileState {
    loaded: boolean;
    profile: Hero;
}

export class ProfileScreen extends React.Component<{heroService: HeroService}, ProfileState> {
    state = {
        loaded: false,
        profile: {
            email: "", name: "", published: false,
        },
    };

    componentDidMount = async () => {
        const profile = await this.props.heroService.fetchMe();
        this.setState({profile, loaded: true});
    }

    handlePublishSubmit = async () => {
        await this.props.heroService.consentToPublish();
        const profile = await this.props.heroService.fetchMe();
        this.setState({profile, loaded: true});
    }

    render() {
        if (!this.state.loaded) {
            return <div>Loading...</div>;
        }
        const profile = this.state.profile;
        if (!profile) {
            return <div>You are not a hero</div>;
        }
        return <>
            <h1>{profile!.name}</h1>

            {profile.published || <button onClick={this.handlePublishSubmit}>I agree to be published</button>}
        </>;
    }
}
