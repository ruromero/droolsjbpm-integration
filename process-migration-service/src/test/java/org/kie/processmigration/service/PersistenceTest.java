package org.kie.processmigration.service;

import java.util.function.Function;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class PersistenceTest {

    protected EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("migration-test");
    protected EntityManager entityManager = entityManagerFactory.createEntityManager();

    protected Function<InjectionPoint, Object> getPCFactory() {
        return ip -> entityManager;
    }

    protected Function<InjectionPoint, Object> getPUFactory() {
        return ip -> entityManagerFactory;
    }
}
