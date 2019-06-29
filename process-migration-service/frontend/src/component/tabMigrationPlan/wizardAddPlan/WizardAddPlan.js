import React from "react";

import { Wizard } from "patternfly-react";
import { Button } from "patternfly-react";
import { Icon } from "patternfly-react";

import WizardBase from "../WizardBase";
import PageReview from "../PageReview";
import { renderWizardSteps } from "../PfWizardRenderers";

import PageMapping from "./PageMapping";
import PageDefinition from "./PageDefinition";
import PagePlanName from "./PagePlanName";

import { AddPlanItems } from "../../common/WizardItems";

export default class WizardAddPlan extends WizardBase {
  constructor(props) {
    super(props);
    this.state = {
      activeStepIndex: 0,
      activeSubStepIndex: 0,
      sourceInfo: "",
      targetInfo: "",
      name: "",
      description: "",
      source: {},
      target: {},
      mappings: "",
      migrationPlanJsonStr: "",
      wizardHeaderTitle: "Add Migration Plan Wizard"
    };
  }

  //using Ref, this is called from parent before open the wizard to reset all the states.
  resetWizardStates() {
    this.setState({
      activeStepIndex: 0,
      activeSubStepIndex: 0,
      sourceInfo: "",
      targetInfo: "",
      name: "",
      description: "",
      source: {},
      target: {},
      mappings: "",
      migrationPlanJsonStr: "",
      editPlanMode: false,
      planId: "",
      wizardHeaderTitle: "Add Migration Plan Wizard"
    });
  }

  //using Ref, this is called from parent before open the wizard for edit existing migration plan
  initialWizardStates(rowData) {
    const jsonStr = JSON.stringify(rowData);
    this.setState({
      activeStepIndex: 0,
      activeSubStepIndex: 0,
      sourceInfo: "",
      targetInfo: "",
      name: rowData.name,
      description: rowData.description,
      source: {
        containerId: rowData.sourceContainerId,
        target: rowData.sourceProcessId
      },
      target: {
        containerId: rowData.targetContainerId,
        processId: rowData.targetProcessId
      },
      mappings: JSON.stringify(rowData.mappings),
      migrationPlanJsonStr: jsonStr,
      editPlanMode: true,
      planId: rowData.id,
      wizardHeaderTitle: "Edit Migration Plan Wizard"
    });
  }

  setSourceDefinition = sourceInfo => this.setState({ sourceInfo });
  setTargetDefinition = targetInfo => this.setState({ targetInfo });

  convertFormDataToJson() {
    const formData = {
      name: this.state.name,
      description: this.state.description,

      sourceContainerId: this.state.source.containerId,
      sourceProcessId: this.state.source.processId,
      targetContainerId: this.state.target.containerId,
      targetProcessId: this.state.target.processId
    };

    if (this.state.mappings !== null && this.state.mappings !== "") {
      formData.mappings = this.state.mappings;
    }

    if (this.state.editPlanMode) {
      formData.id = this.state.planId;
    }

    const jsonStr = JSON.stringify(formData, null, 2);

    this.setState({ migrationPlanJsonStr: jsonStr });
  }

  onSubmitMigrationPlan = () => {
    //call the addPlan. addPlan need to be in the parent because it's shared between WizardAddPlan and Import Plan pop-up
    if (!this.state.editPlanMode) {
      this.props.addPlan(this.state.migrationPlanJsonStr);
    } else {
      this.props.editPlan(this.state.migrationPlanJsonStr, this.state.planId);
    }
    this.onNextButtonClick();
  };

  onChangeSource = (newContainerId, newProcessId) => {
    this.setState({
      source: {
        containerId: newContainerId,
        processId: newProcessId
      }
    });
  };

  onChangeTarget = (newContainerId, newProcessId) => {
    this.setState({
      target: {
        containerId: newContainerId,
        processId: newProcessId
      }
    });
  };

  render() {
    const {
      activeStepIndex,
      activeSubStepIndex,
      sourceInfo,
      targetInfo,
      migrationPlanJsonStr
    } = this.state;

    const renderAddPlanWizardContents = wizardSteps => {
      return wizardSteps.map((step, stepIndex) =>
        step.subSteps.map((sub, subStepIndex) => {
          if (stepIndex === 0) {
            // render steps 1
            return (
              <Wizard.Contents
                key={subStepIndex}
                stepIndex={stepIndex}
                subStepIndex={subStepIndex}
                activeStepIndex={activeStepIndex}
                activeSubStepIndex={activeSubStepIndex}
              >
                <PagePlanName
                  name={this.state.name}
                  description={this.state.description}
                />
              </Wizard.Contents>
            );
          } else if (stepIndex === 1) {
            // render steps 2
            return (
              <Wizard.Contents
                key={subStepIndex}
                stepIndex={stepIndex}
                subStepIndex={subStepIndex}
                activeStepIndex={activeStepIndex}
                activeSubStepIndex={activeSubStepIndex}
              >
                <PageDefinition
                  sourceInfo={sourceInfo}
                  targetInfo={targetInfo}
                  setSourceDefinition={this.setSourceDefinition}
                  setTargetDefinition={this.setTargetDefinition}
                  source={this.state.source}
                  target={this.state.target}
                  onChangeSource={this.onChangeSource}
                  onChangeTarget={this.onChangeTarget}
                  kieServerId={this.props.kieServerId}
                />
              </Wizard.Contents>
            );
          } else if (stepIndex === 2) {
            // render steps 3
            return (
              <Wizard.Contents
                key={subStepIndex}
                stepIndex={stepIndex}
                subStepIndex={subStepIndex}
                activeStepIndex={activeStepIndex}
                activeSubStepIndex={activeSubStepIndex}
              >
                <PageMapping
                  sourceInfo={sourceInfo}
                  targetInfo={targetInfo}
                  mappings={this.state.mappings}
                  onMappingsChange={mappings => this.setState({ mappings })}
                />
              </Wizard.Contents>
            );
          } else if (stepIndex === 3) {
            // render review
            return (
              <Wizard.Contents
                key={subStepIndex}
                stepIndex={stepIndex}
                subStepIndex={subStepIndex}
                activeStepIndex={activeStepIndex}
                activeSubStepIndex={activeSubStepIndex}
              >
                <PageReview inputJsonStr={migrationPlanJsonStr} />
              </Wizard.Contents>
            );
          } else if (stepIndex === 4) {
            // render mock progress
            return (
              <Wizard.Contents
                key={subStepIndex}
                stepIndex={stepIndex}
                subStepIndex={subStepIndex}
                activeStepIndex={activeStepIndex}
                activeSubStepIndex={activeSubStepIndex}
              >
                <PageReview inputJsonStr={this.props.addPlanResponseJsonStr} />
              </Wizard.Contents>
            );
          }
          return null;
        })
      );
    };

    return (
      <div>
        <form
          className="form-horizontal"
          name="form_migration_plan"
          id="WizardAddPlan_id_form1"
        >
          <Wizard
            show={this.props.showPlanWizard}
            onHide={this.props.closeAddPlanWizard}
          >
            <Wizard.Header
              onClose={this.props.closeAddPlanWizard}
              title={this.state.wizardHeaderTitle}
            />
            <Wizard.Body>
              <Wizard.Steps
                steps={renderWizardSteps(
                  AddPlanItems,
                  activeStepIndex,
                  activeSubStepIndex,
                  this.onStepClick
                )}
              />
              <Wizard.Row>
                <Wizard.Main>
                  {renderAddPlanWizardContents(AddPlanItems)}
                </Wizard.Main>
              </Wizard.Row>
            </Wizard.Body>
            <Wizard.Footer>
              <Button
                bsStyle="default"
                className="btn-cancel"
                onClick={this.props.closeAddPlanWizard}
              >
                Cancel
              </Button>
              <Button
                bsStyle="default"
                disabled={activeStepIndex === 0 && activeSubStepIndex === 0}
                onClick={this.onBackButtonClick}
              >
                <Icon type="fa" name="angle-left" />
                Back
              </Button>
              {(activeStepIndex === 0 || activeStepIndex === 2) && (
                <Button bsStyle="primary" onClick={this.onNextButtonClick}>
                  Next
                  <Icon type="fa" name="angle-right" />
                </Button>
              )}

              {activeStepIndex === 1 && (
                <Button
                  bsStyle="primary"
                  disabled={
                    this.state.sourceInfo == "" || this.state.targetInfo == ""
                  }
                  onClick={this.onNextButtonClick}
                >
                  Next
                  <Icon type="fa" name="angle-right" />
                </Button>
              )}
              {activeStepIndex === 3 && (
                <Button bsStyle="primary" onClick={this.onSubmitMigrationPlan}>
                  Submit Plan
                  <Icon type="fa" name="angle-right" />
                </Button>
              )}
              {activeStepIndex === 4 && (
                <Button
                  bsStyle="primary"
                  onClick={this.props.closeAddPlanWizard}
                >
                  Close
                  <Icon type="fa" name="angle-right" />
                </Button>
              )}
            </Wizard.Footer>
          </Wizard>
        </form>
      </div>
    );
  }
}
