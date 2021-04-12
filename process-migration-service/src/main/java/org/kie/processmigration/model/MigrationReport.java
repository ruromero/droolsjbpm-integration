/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.processmigration.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.kie.server.api.model.admin.MigrationReportInstance;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "migration_reports", indexes = {@Index(columnList = "migration_id")})
@SequenceGenerator(name = "migRepIdSeq", sequenceName = "MIG_REP_ID_SEQ")
@EqualsAndHashCode(callSuper = false)
@ToString
@Accessors(chain = true)
@Getter
@Setter
public class MigrationReport extends PanacheEntity {

    private static final long serialVersionUID = 5817223334991683064L;

    @Column(name = "migration_id")
    private Long migrationId;

    @Column(name = "process_instance_id")
    private Long processInstanceId;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "success") // successful is a reserved word in Oracle DB
    private Boolean successful;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "log")
    @Lob
    @CollectionTable(
            name = "migration_report_logs",
            joinColumns = @JoinColumn(name = "report_id")
    )
    private List<String> logs;

    public MigrationReport() {
    }

    public MigrationReport(Long migrationId, MigrationReportInstance reportInstance) {
        this.migrationId = migrationId;
        this.processInstanceId = reportInstance.getProcessInstanceId();
        if (reportInstance.getStartDate() != null) {
            this.startDate = reportInstance.getStartDate().toInstant();
        }
        if (reportInstance.getEndDate() != null) {
            this.endDate = reportInstance.getEndDate().toInstant();
        }
        this.successful = reportInstance.isSuccessful();
        this.logs = new ArrayList<>(reportInstance.getLogs());
    }

}
