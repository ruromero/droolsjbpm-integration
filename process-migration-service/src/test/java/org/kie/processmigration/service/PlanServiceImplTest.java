package org.kie.processmigration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Rule;
import org.junit.Test;
import org.kie.processmigration.model.Plan;
import org.kie.processmigration.service.impl.PlanServiceImpl;


public class PlanServiceImplTest extends PersistenceTest {

    @Rule
    public WeldInitiator weld = WeldInitiator
                                             .from(PlanServiceImpl.class)
                                             .activate(ApplicationScoped.class)
                                             .setPersistenceContextFactory(getPCFactory())
                                             .build();
    @Inject
    private PlanService planService;

    @Test
    public void testSaveAndFindAll() {
        assertNotNull(planService);

        Plan plan = new Plan();
        plan.setSourceContainerId("containerId");
        plan.setName("name");
        plan.setTargetContainerId("targetContainerId");
        plan.setDescription("description");

        List<Plan> plans = planService.findAll();

        assertNotNull(plans);
        assertEquals(1, plans.size());
        
    }

}
