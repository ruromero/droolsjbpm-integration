package org.kie.processmigration.service.impl;

import java.util.Date;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.kie.processmigration.model.Credentials;
import org.kie.processmigration.model.Migration;
import org.kie.processmigration.service.CredentialsService;
import org.kie.processmigration.service.MigrationService;
import org.kie.processmigration.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class EjbTimerSchedulerServiceImpl implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(EjbTimerSchedulerServiceImpl.class);

    @Resource
    private TimerService timerService;

    @Inject
    private MigrationService migrationService;

    @Inject
    private CredentialsService credentialsService;

    @Timeout
    public void doMigration(Timer timer) {
        Long migrationId = (Long) timer.getInfo();
        Migration migration = migrationService.get(migrationId);
        migrationService.migrate(migration);
    }

    @Override
    public void scheduleMigration(Migration migration, Credentials credentials) {
        Long migrationId = migration.getId();
        credentialsService.save(credentials.setMigrationId(migrationId));
        if (migration.getDefinition().getExecution().getScheduledStartTime() == null) {
            timerService.createTimer(new Date(), migrationId);
        } else {
            timerService.createTimer(migration.getDefinition().getExecution().getScheduledStartTime(), migrationId);
        }
    }

    @PreDestroy
    public void stopAll() {
        for (Timer timer : timerService.getTimers()) {
            logger.info("Stopping timer: {}", timer.getInfo());
            timer.cancel();
        }
    }
}
