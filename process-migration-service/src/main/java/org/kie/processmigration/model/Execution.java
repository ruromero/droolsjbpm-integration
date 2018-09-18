/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kie.processmigration.model;

import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class Execution {

    public enum ExecutionType {
        ASYNC,
        SYNC
    }

    public enum ExecutionStatus {
        SCHEDULED,
        STARTED,
        COMPLETED,
        COMPLETED_WITH_ERRORS,
        CANCELLED,
        CREATED
    }

    @Column(name = "execution_type")
    private ExecutionType type;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("callback_url")
    @Column(name = "callback_url")
    private URI callbackUrl;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("scheduled_start_time")
    @Column(name = "scheduled_start_time")
    private Date scheduledStartTime;

    public Date getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(Date scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public ExecutionType getType() {
        return type;
    }

    public void setType(ExecutionType type) {
        this.type = type;
    }

    public URI getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(URI callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

}
