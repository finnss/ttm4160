import Frontpage from "./frontpage";
import React from "react";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import Book from "./book";

export default function Routes() {
  return (
    <Router>
      <Switch>
        <Route path="/book">
          <Book />
        </Route>
        <Route path="/">
          <Frontpage />
        </Route>
      </Switch>
    </Router>
  );
}
