import React from "react";
import PropTypes from "prop-types";

import { Button, Icon, MessageDialog } from "patternfly-react";

import MigrationPlansBase from "./MigrationPlansBase";
import MigrationPlansTable from "./MigrationPlansTable";
import MigrationPlanListFilter from "./MigrationPlanListFilter";

import WizardAddPlan from "./wizardAddPlan/WizardAddPlan";
import planClient from "../../clients/planClient";
import ImportPlanModal from "./ImportPlanModal";

export default class MigrationPlans extends MigrationPlansBase {
  constructor(props) {
    super(props);
    this.state = {
      filteredPlans: [],
      plan: this.getDefaultPlan(),
      showDeleteConfirmation: false,
      showMigrationWizard: false,
      showPlanWizard: false,
      deletePlanId: "",
      runningInstances: [],
      addPlanResponseJsonStr: ""
    };
  }

  getDefaultPlan = () => {
    return {
      source: {},
      target: {},
      mappings: {}
    };
  };

  closeMigrationWizard = () => {
    this.setState({ showMigrationWizard: false });
  };

  openAddPlanWizard = id => {
    if (id) {
      planClient.get(id).then(plan => {
        this.setState({ plan, showPlanWizard: true });
      });
    } else {
      this.setState({ showPlanWizard: true });
    }
  };

  closeAddPlanWizard = () => {
    this.setState({
      showPlanWizard: false,
      plan: this.getDefaultPlan()
    });
    this.retrieveAllPlans();
  };

  onFilterChange = planFilter => {
    let filteredPlans = this.state.plans;
    filteredPlans = filteredPlans.filter(plan => {
      let sourceContainterId = plan.source.containerId.toLowerCase();
      return sourceContainterId.indexOf(planFilter.toLowerCase()) !== -1;
    });
    this.setState({
      filteredPlans
    });
  };

  render() {
    const { showPlanWizard } = this.state;

    //for MessageDialogDeleteConfirmation
    const primaryContent = (
      <p className="lead">
        Please confirm you will delete this migration plan{" "}
        {this.state.deletePlanId}
      </p>
    );
    const secondaryContent = <p />;
    const icon = <Icon type="pf" name="error-circle-o" />;

    return (
      <React.Fragment>
        {/* Delete Plan pop-up */}
        <MessageDialog
          show={this.state.showDeleteConfirmation}
          onHide={this.hideDeleteDialog}
          primaryAction={this.deletePlan}
          secondaryAction={this.hideDeleteDialog}
          primaryActionButtonContent="Delete"
          secondaryActionButtonContent="Cancel"
          primaryActionButtonBsStyle="danger"
          title="Delete Migration Plan"
          icon={icon}
          primaryContent={primaryContent}
          secondaryContent={secondaryContent}
          accessibleName="deleteConfirmationDialog"
          accessibleDescription="deleteConfirmationDialogContent"
        />
        <br />
        {/* import plan & Add Plan */}
        <div className="row">
          <div className="col-xs-9">
            <MigrationPlanListFilter onChange={this.onFilterChange} />
          </div>
          <div className="col-xs-3">
            <div className="pull-right">
              <ImportPlanModal onImport={this.importPlan} />
              &nbsp;
              <Button
                bsStyle="primary"
                onClick={() => this.openAddPlanWizard()}
              >
                Add Plan
              </Button>
            </div>
          </div>
        </div>
        <br />
        {/* Table lists all the migration plans */}
        <MigrationPlansTable
          plans={this.state.filteredPlans}
          kieServerId={this.props.kieServerId}
          onEditPlan={id => this.openAddPlanWizard(id)}
        />

        {/* Add Plan Wizard */}
        {showPlanWizard && (
          <WizardAddPlan
            closeAddPlanWizard={this.closeAddPlanWizard}
            onSavePlan={this.savePlan}
            onPlanChanged={plan => this.setState({ plan })}
            kieServerId={this.props.kieServerId}
            plan={this.state.plan}
          />
        )}
      </React.Fragment>
    );
  }
}

MigrationPlans.propTypes = {
  kieServerId: PropTypes.string.isRequired
};
