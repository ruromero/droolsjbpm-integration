package org.kie.processmigration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.server.api.model.admin.MigrationReportInstance;

@Entity
@Table(name = "migration_reports", indexes = {@Index(columnList = "migration_id")})
@NamedQueries({
               @NamedQuery(name = "MigrationReport.findByMigrationId", query = "SELECT p FROM MigrationReport p WHERE p.migrationId = :id")
})
public class MigrationReport implements Serializable {

    private static final long serialVersionUID = 5817223334991683064L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("migration_id")
    @Column(name = "migration_id")
    private Long migrationId;

    @JsonProperty("process_instance_id")
    @Column(name = "process_instance_id")
    private Long processInstanceId;

    @JsonProperty("start_date")
    @Column(name = "start_date")
    private Date startDate;

    @JsonProperty("end_date")
    @Column(name = "end_date")
    private Date endDate;

    private Boolean successful;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "log", columnDefinition = "TEXT")
    @CollectionTable(
                     name = "migration_report_logs",
                     joinColumns = @JoinColumn(name = "report_id")
    )
    private List<String> logs;

    public MigrationReport() {}

    public MigrationReport(Long migrationId, MigrationReportInstance reportInstance) {
        this.migrationId = migrationId;
        this.processInstanceId = reportInstance.getProcessInstanceId();
        this.startDate = reportInstance.getStartDate();
        this.endDate = reportInstance.getEndDate();
        this.logs = new ArrayList<>(reportInstance.getLogs());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(Long migrationId) {
        this.migrationId = migrationId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

}
