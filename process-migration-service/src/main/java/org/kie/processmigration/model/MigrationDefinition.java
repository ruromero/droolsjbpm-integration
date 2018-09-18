/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kie.processmigration.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class MigrationDefinition {

    @JsonProperty("plan_id")
    @Column(name = "plan_id")
    private Long planId;

    @JsonProperty("process_instance_ids")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
                     name = "process_instance_ids",
                     joinColumns = @JoinColumn(name = "migration_definition_id")
    )
    private List<Long> processInstanceIds;

    @Embedded
    private Execution execution;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<Long> getProcessInstanceIds() {
        return processInstanceIds;
    }

    public void setProcessInstanceIds(List<Long> processInstanceIds) {
        if (processInstanceIds != null) {
            this.processInstanceIds = new ArrayList<>(processInstanceIds);
        } else {
            this.processInstanceIds = null;
        }
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

}
