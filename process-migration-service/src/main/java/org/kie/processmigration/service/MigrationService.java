package org.kie.processmigration.service;

import java.util.List;

import org.kie.processmigration.model.Credentials;
import org.kie.processmigration.model.Migration;
import org.kie.processmigration.model.MigrationDefinition;
import org.kie.processmigration.model.MigrationReport;

public interface MigrationService {

    Migration get(Long id);

    List<MigrationReport> getResults(Long id);

    List<Migration> findAll();

    Migration submit(MigrationDefinition definition, Credentials credentials);

    Migration update(Long id, MigrationDefinition migration);

    Migration delete(Long id);
    
    Migration migrate(Migration migration);

}
