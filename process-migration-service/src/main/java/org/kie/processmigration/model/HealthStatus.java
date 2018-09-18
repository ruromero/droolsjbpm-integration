package org.kie.processmigration.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class HealthStatus {

    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    private String status;

    @JsonInclude(Include.NON_NULL)
    private String message;

    private Date date = new Date();

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public HealthStatus setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public HealthStatus up() {
        this.status = UP;
        return this;
    }

    public HealthStatus down() {
        this.status = DOWN;
        return this;
    }

    @JsonIgnore
    public boolean isUp() {
        return UP.equals(this.status);
    }

}
