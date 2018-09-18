package org.kie.processmigration.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kie.processmigration.model.Execution.ExecutionStatus;
import org.kie.processmigration.model.Execution.ExecutionType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "migrations")
@NamedQueries({
               @NamedQuery(name = "Migration.findAll", query = "SELECT p FROM Migration p"),
               @NamedQuery(name = "Migration.findById", query = "SELECT p FROM Migration p WHERE p.id = :id")
})
public class Migration implements Serializable {

    private static final long serialVersionUID = 7212317252498596171L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MigrationDefinition definition;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    private Date createdAt;

    @JsonProperty("started_at")
    @Column(name = "started_at")
    private Date startedAt;

    @JsonProperty("finished_at")
    @Column(name = "finished_at")
    private Date finishedAt;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("cancelled_at")
    @Column(name = "cancelled_at")
    private Date cancelledAt;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("error_message")
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    private ExecutionStatus status;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "migration_id")
    private List<MigrationReport> reports;

    public Migration() {}

    public Migration(MigrationDefinition definition) {
        this.definition = definition;
        Date now = new Date();
        createdAt = now;
        if (ExecutionType.ASYNC.equals(definition.getExecution().getType()) &&
            definition.getExecution().getScheduledStartTime() != null &&
            now.before(definition.getExecution().getScheduledStartTime())) {
            status = Execution.ExecutionStatus.SCHEDULED;
        } else {
            status = Execution.ExecutionStatus.CREATED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MigrationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(MigrationDefinition definition) {
        this.definition = definition;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<MigrationReport> getReports() {
        return reports;
    }

    public void setReports(List<MigrationReport> reports) {
        this.reports = reports;
    }

    public Migration start() {
        startedAt = new Date();
        status = ExecutionStatus.STARTED;
        return this;
    }

    public Migration complete(Boolean hasErrors) {
        finishedAt = new Date();
        if (Boolean.TRUE.equals(hasErrors)) {
            status = ExecutionStatus.COMPLETED_WITH_ERRORS;
        } else {
            status = ExecutionStatus.COMPLETED;
        }
        return this;
    }

    public Migration cancel() {
        cancelledAt = new Date();
        status = ExecutionStatus.CANCELLED;
        return this;
    }

    public Migration fail(Exception e) {
        finishedAt = new Date();
        status = ExecutionStatus.COMPLETED_WITH_ERRORS;
        errorMessage = e.toString();
        return this;
    }

}
