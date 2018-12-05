import React from "react";
import ReactDOM from "react-dom";
import { App } from "./app";

import { MockHeroService } from "./services/mockHeroService";

ReactDOM.render(<App heroService={new MockHeroService()} />, document.getElementById("app"));
