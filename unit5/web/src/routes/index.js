import Frontpage from "./frontpage";
import React from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  useHistory
} from "react-router-dom";
import Reserve from "./reserve";
import Confirmation from "./confirmation";
import Reservations from "./reservations";
import About from "./about";
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
        <Route path="/confirmation">
          <Confirmation {...props} />
        </Route>
        <Route path="/reservations">
          <Reservations {...props} />
        </Route>
        <Route path="/about">
          <About {...props} />
        </Route>
        <Route path="/">
          <Frontpage {...props} />
        </Route>
      </Switch>
    </Router>
  );
}
