import React from "react";
import renderer from "react-test-renderer";
import { ProfileScreen } from "../src/app/profile";
import { MockHeroService } from "../src/services/mockHeroService";

function promiseCompletion() {
    return new Promise((resolve) => setImmediate(resolve));
}

describe("profile screen", () => {

    it("shows profile screen", async () => {
        const app = renderer.create(<ProfileScreen heroService={new MockHeroService()} />);
        await promiseCompletion();
        expect(app.toJSON()).toMatchSnapshot();
    });

});
