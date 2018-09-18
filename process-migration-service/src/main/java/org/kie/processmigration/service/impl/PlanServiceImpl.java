package org.kie.processmigration.service.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.kie.processmigration.model.Plan;
import org.kie.processmigration.service.PlanService;

@ApplicationScoped
public class PlanServiceImpl implements PlanService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Plan get(Long id) {
        TypedQuery<Plan> query = em.createNamedQuery("Plan.findById", Plan.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Plan> findAll() {
        return em.createNamedQuery("Plan.findAll", Plan.class).getResultList();
    }

    @Override
    @Transactional
    public Plan delete(Long id) {
        Plan plan = em.find(Plan.class, id);
        em.remove(plan);
        return plan;
    }

    @Override
    @Transactional
    public Plan save(Plan plan) {
        em.persist(plan);
        return plan;
    }

}
