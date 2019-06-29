const express = require("express");
const webpackDevMiddleware = require("webpack-dev-middleware");
const webpack = require("webpack");
const webpackConfig = require("./webpack.config.js");
const app = express();
const fs = require("fs");

const compiler = webpack(webpackConfig);

app.use(
  webpackDevMiddleware(compiler, {
    hot: true,
    filename: "bundle.js",
    publicPath: "/",
    stats: {
      colors: true
    },
    historyApiFallback: true
  })
);

// eslint-disable-next-line no-undef
app.use(express.static(__dirname));

const server = app.listen(3000, function() {
  const host = server.address().address;
  const port = server.address().port;
  console.log("PIM app listening at http://%s:%s", host, port);
});

app.get("/rest/kieservers", (req, res) => {
  return res.send(readFile("kieservers.json"));
});

app.get("/rest/kieservers/instances", (req, res) => {
  return res.send(readFile("running_instances.json"));
});

app.get(
  "/rest/kieservers/:kieServerId/definitions/:containerId/:processId",
  (req, res) => {
    if (req.params.containerId === "mock_error") {
      if (req.params.processId === "notfound") {
        return res.status(404).send("Not found");
      } else {
        return res.status(500).send({
          message: {
            string: "Behold the error."
          }
        });
      }
    }
    if (req.params.containerId.startsWith("Mortgage")) {
      return res.send(readFile("process_definition_mortgage.json"));
    }
    return res.send(readFile("process_definition_evaluation.json"));
  }
);

app.get("/rest/kieservers/:kieServerId/definitions", (req, res) => {
  return res.send(readFile("process_definitions.json"));
});

app.get("/rest/plans", (req, res) => {
  return res.send(readFile("plans.json"));
});

app.post("/rest/plans", (req, res) => {
  return res.send(readFile("plan.json"));
});

app.put("/rest/plans/:planId", (req, res) => {
  console.log(`Updated plan with ID: ${req.params.planId}`);
  return res.send(readFile("plan.json"));
});

app.get("/rest/plans/:planId", (req, res) => {
  console.log(`Retrieved plan with ID: ${req.params.planId}`);
  return res.send(readFile("plan.json"));
});

app.delete("/rest/plans/:planId", (req, res) => {
  console.log(`Deleted plan with ID: ${req.params.planId}`);
  return res.send(readFile("plans.json"));
});

app.get("/rest/migrations", (req, res) => {
  return res.send(readFile("migrations.json"));
});

app.post("/rest/migrations", (req, res) => {
  return res.send(readFile("migration.json"));
});

app.get("/rest/migrations/:migrationId", (req, res) => {
  console.log(`Retrieved migration with ID: ${req.params.migrationId}`);
  return res.send(readFile("migration.json"));
});

app.put("/rest/migrations/:migrationId", (req, res) => {
  console.log(`Updated migration with ID: ${req.params.migrationId}`);
  return res.send(readFile("migration.json"));
});

app.delete("/rest/migrations/:migrationId", (req, res) => {
  console.log(`Deleted migration with ID: ${req.params.migrationId}`);
  return res.send(readFile("migrations.json"));
});

app.get("/rest/migrations/:migrationId/results", (req, res) => {
  console.log(`Retrieved migration results with ID: ${req.params.migrationId}`);
  return res.send(readFile("migration_results.json"));
});

function readFile(fileName) {
  return JSON.parse(fs.readFileSync("./mock_data/" + fileName));
}
