import React from "react";
import ReactDOM from "react-dom";
import { App } from "./app";

import { HeroServiceHttp } from "./services/heroServiceHttp";

ReactDOM.render(<App heroService={new HeroServiceHttp()} />, document.getElementById("app"));
