import Frontpage from "./frontpage";
import React from "react";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import Reserve from "./reserve";
import { Link } from "react-router-dom";

export default function Routes(props) {
  return (
    <Router>
      <h1 className="title">
        <Link style={{ color: "white" }} to="/">
          Occupee
        </Link>
      </h1>
      <Switch>
        <Route path="/reserve">
          <Reserve {...props} />
        </Route>
        <Route path="/">
          <Frontpage {...props} />
        </Route>
      </Switch>
    </Router>
  );
}
