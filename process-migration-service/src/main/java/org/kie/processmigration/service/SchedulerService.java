package org.kie.processmigration.service;

import org.kie.processmigration.model.Credentials;
import org.kie.processmigration.model.Migration;

public interface SchedulerService {

    void scheduleMigration(Migration migration, Credentials credentials);

}
