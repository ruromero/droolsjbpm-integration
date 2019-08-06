import React from "react";
import validator from "validator";

import {
  Icon,
  MessageDialog,
  OverlayTrigger,
  Tooltip,
  Table,
  actionHeaderCellFormatter
} from "patternfly-react";

import PageViewMigrationLogs from "./PageViewMigrationLogs";
import PageEditMigrationDefinitionModal from "./PageEditMigrationDefinitionModal";
import MigrationClient from "../../clients/migrationClient";
import { HelpBlock } from "patternfly-react/dist/js/components/Form";

export default class MigrationDefinitions extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      migrationsDefinitions: [],
      migrationLogs: [],
      showLogDialog: false,
      showDeleteConfirmation: false,
      deleteMigrationId: "",
      editMigrationId: "",
      validationMessage: ""
    };
  }

  componentDidMount() {
    this.retrieveMigrationDefinitions();
  }

  hideDetailDialog = () => {
    this.setState({
      showLogDialog: false
    });
  };

  retrieveMigrationLogs = rowData => {
    MigrationClient.getResults(rowData.id).then(migrationLogs => {
      this.setState({
        showLogDialog: true,
        migrationLogs
      });
    });
  };

  showDeleteDialog = id => {
    this.setState({
      showDeleteConfirmation: true,
      deleteMigrationId: id
    });
  };

  hideDeleteDialog = () => {
    this.setState({
      showDeleteConfirmation: false,
      deleteMigrationId: ""
    });
  };

  deleteMigration = () => {
    MigrationClient.delete(this.state.deleteMigrationId).then(() => {
      this.hideDeleteDialog();
      this.retrieveMigrationDefinitions();
    });
  };

  retrieveMigrationDefinitions = event => {
    const input = document.getElementById("id_migrationsDefinitions_input1");
    if (
      input != null &&
      input.value != null &&
      input.value != "" &&
      !validator.isNumeric(input.value)
    ) {
      //during user input, need to check for migration id should be numeric
      this.setState({
        validationMessage: "Error: migration id should be numeric"
      });
    } else {
      //all good, so no need to outpt validation error message
      this.setState({
        validationMessage: ""
      });
      //search the migration record
      let migrationRequest;
      if (input != null) {
        if (
          event != null &&
          event.currentTarget.id == "id_migrationDefinition_refresh_button"
        ) {
          //For refresh, just retrieve all records
          migrationRequest = MigrationClient.getAll();
        } else {
          migrationRequest = MigrationClient.get(input.value);
        }

        migrationRequest.then(migrationsDefinitions => {
          if (migrationsDefinitions != null) {
            const tmpStr = JSON.stringify(migrationsDefinitions);
            if (tmpStr != "" && tmpStr.charAt(0) != "[") {
              //this is single element json, need to change to json array, otherwise the table won't display
              migrationsDefinitions = [migrationsDefinitions];
            }
          }
          this.setState({
            migrationsDefinitions
          });
        });
      }
    }
  };

  render() {
    const headerFormat = value => <Table.Heading>{value}</Table.Heading>;
    const cellFormat = value => <Table.Cell>{value}</Table.Cell>;

    const tooltipDelete = (
      <Tooltip id="tooltip">
        <div>Delete</div>
      </Tooltip>
    );

    const tooltipRefresh = (
      <Tooltip id="tooltip">
        <div>Refresh All Migration Definitions</div>
      </Tooltip>
    );
    const resultBootstrapColumns = [
      {
        header: {
          label: "ID",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [cellFormat]
        },
        property: "id"
      },
      {
        header: {
          label: "Status",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [
            (value, { rowData }) => [
              <DisplayStatus
                key="0"
                rowData={rowData}
                retrieveMigrationLogs={this.retrieveMigrationLogs}
              />
            ]
          ]
        },
        property: "status"
      },
      {
        header: {
          label: "Created At",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [cellFormat]
        },
        property: "createdAt"
      },
      {
        header: {
          label: "Started At",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [cellFormat]
        },
        property: "startedAt"
      },
      {
        header: {
          label: "Finished At",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [cellFormat]
        },
        property: "finishedAt"
      },
      {
        header: {
          label: "Scheduled At",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [
            (value, { rowData }) => [
              <Table.Cell key="0">
                {rowData.definition.execution.scheduledStartTime}
              </Table.Cell>
            ]
          ]
        },
        property: "definition.planId"
      },
      {
        header: {
          label: "Error Message",
          formatters: [headerFormat]
        },
        cell: {
          formatters: [cellFormat]
        },
        property: "errorMessage"
      },
      {
        header: {
          label: "Actions",
          props: {
            rowSpan: 1,
            colSpan: 2
          },
          formatters: [actionHeaderCellFormatter]
        },
        cell: {
          formatters: [
            (value, { rowData }) => [
              <Table.Actions key="0">
                <OverlayTrigger overlay={tooltipDelete} placement={"bottom"}>
                  <Table.Button
                    bsStyle="link"
                    onClick={() => this.showDeleteDialog(rowData.id)}
                  >
                    <Icon type="fa" name="trash" />
                  </Table.Button>
                </OverlayTrigger>
              </Table.Actions>,
              <DisplayActions key="1" rowData={rowData} />
            ]
          ]
        },
        property: "action"
      }
    ];

    //for View migration logs pop-up
    const primaryContent = (
      <PageViewMigrationLogs migrationLogs={this.state.migrationLogs} />
    );
    const secondaryContent = <p />;
    const icon = <Icon type="pf" name="info" />;

    //for Delete migration definition pop-up
    const primaryDeleteContent = (
      <p className="lead">
        Please confirm you will delete this migration:{" "}
        {this.state.deleteMigrationId}
      </p>
    );
    const deleteIcon = <Icon type="pf" name="error-circle-o" />;

    //only "SCHEDULED" migrations allow the "Edit" button
    function DisplayActions(props) {
      const rowData = props.rowData;
      if (rowData.status == "SCHEDULED") {
        return (
          <Table.Actions key="1">
            <PageEditMigrationDefinitionModal migrationId={rowData.id} />
          </Table.Actions>
        );
      } else {
        return <Table.Actions key="1" />;
      }
    }

    //for status other than "SCHEDULED" enable the link to check migration logs
    function DisplayStatus(props) {
      const rowData = props.rowData;
      if (rowData.status == "SCHEDULED") {
        return <Table.Cell key="0">{rowData.status}</Table.Cell>;
      } else {
        return (
          <Table.Cell key="0">
            <a href="#" onClick={() => props.retrieveMigrationLogs(rowData)}>
              {rowData.status}
            </a>
          </Table.Cell>
        );
      }
    }

    return (
      <React.Fragment>
        {/* View migration logs pop-up */}
        <MessageDialog
          show={this.state.showLogDialog}
          onHide={this.hideDetailDialog}
          primaryAction={this.hideDetailDialog}
          primaryActionButtonContent="Close"
          title="View Migration Logs"
          icon={icon}
          primaryContent={primaryContent}
          secondaryContent={secondaryContent}
          accessibleName="viewMigrationLogsDialog"
          accessibleDescription="migrationDetailDialogContent"
          className="kie-pim-dialog--wide"
        />

        {/* Delete migration definition pop-up */}
        <MessageDialog
          show={this.state.showDeleteConfirmation}
          onHide={this.hideDeleteDialog}
          primaryAction={this.deleteMigration}
          secondaryAction={this.hideDeleteDialog}
          primaryActionButtonContent="Delete"
          secondaryActionButtonContent="Cancel"
          primaryActionButtonBsStyle="danger"
          title="Delete Migration Definition"
          icon={deleteIcon}
          primaryContent={primaryDeleteContent}
          accessibleName="deleteConfirmationDialog"
          accessibleDescription="deleteConfirmationDialogContent"
        />
        <br />
        <div className="row">
          <div className="col-xs-9">
            <input
              id="id_migrationsDefinitions_input1"
              type="search"
              placeholder="Search By Migration ID"
            />
            <button
              type="button"
              onClick={this.retrieveMigrationDefinitions}
              id="id_migrationDefinition_search_button"
            >
              <span className="fa fa-search" />
            </button>
            {this.state.validationMessage && (
              <HelpBlock>{this.state.validationMessage}</HelpBlock>
            )}
          </div>
          <div className="col-xs-3">
            <div className="pull-right">
              <OverlayTrigger overlay={tooltipRefresh} placement={"bottom"}>
                <button
                  type="button"
                  onClick={this.retrieveMigrationDefinitions}
                  id="id_migrationDefinition_refresh_button"
                >
                  <span className="fa fa-refresh" />
                </button>
              </OverlayTrigger>
            </div>
          </div>
        </div>
        <br />
        <Table.PfProvider striped hover columns={resultBootstrapColumns}>
          <Table.Header />
          <Table.Body rows={this.state.migrationsDefinitions} rowKey="id" />
        </Table.PfProvider>
      </React.Fragment>
    );
  }
}
