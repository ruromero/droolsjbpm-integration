package org.kie.processmigration.service;

import java.util.List;

import org.kie.processmigration.model.Plan;

public interface PlanService {

    Plan get(Long id);

    List<Plan> findAll();

    Plan delete(Long id);

    Plan save(Plan plan);

}
