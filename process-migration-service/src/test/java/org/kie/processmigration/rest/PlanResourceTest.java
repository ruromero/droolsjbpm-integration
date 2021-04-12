/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.processmigration.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.processmigration.model.Plan;
import org.kie.processmigration.model.ProcessRef;
import org.kie.processmigration.model.exceptions.PlanNotFoundException;
import org.kie.processmigration.service.PlanService;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class PlanResourceTest {

    @InjectMock
    PlanService planService;

    @BeforeEach
    void init() {
        RestAssured.basePath = "/rest";
    }

    @Test
    void testGetAll() {
        given()
                .when()
                .get("/plans")
                .then().statusCode(HttpStatus.SC_OK)
                .body("", hasSize(0));

        List<Plan> plans = new ArrayList<>();
        plans.add(new Plan().setName("plan1")
                .setSource(new ProcessRef().setContainerId("container-1.0").setProcessId("process.1"))
                .setTarget(new ProcessRef().setContainerId("container-2.0").setProcessId("process.1"))
                .setDescription("plan 1 description")
                .setMappings(Map.of("node1", "node2", "nodeA", "nodeB")));
        plans.add(new Plan().setName("plan2")
                .setSource(new ProcessRef().setContainerId("container-1.0").setProcessId("process.2"))
                .setTarget(new ProcessRef().setContainerId("container-2.0").setProcessId("process.2"))
                .setDescription("plan 2 description"));
        Mockito.when(planService.findAll()).thenReturn(plans);

        Plan[] results = given()
                .when()
                .get("/plans")
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(Plan[].class);
        assertThat(plans, hasItems(results));
    }

    @Test
    void testGet() throws PlanNotFoundException {
        Plan plan = new Plan().setName("plan1")
                .setSource(new ProcessRef().setContainerId("container-1.0").setProcessId("process.1"))
                .setTarget(new ProcessRef().setContainerId("container-2.0").setProcessId("process.1"))
                .setDescription("plan description")
                .setMappings(Map.of("node1", "node2", "nodeA", "nodeB"));
        Mockito.when(planService.get(1L)).thenThrow(new PlanNotFoundException(1L)).thenReturn(plan);
        given()
                .when()
                .get("/plans/1")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);

        Plan result = given()
                .when()
                .get("/plans/1")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(Plan.class);

        assertThat(result, is(plan));
    }

    @Test
    void testCreate() {
        Plan plan = new Plan().setName("plan1")
                .setSource(new ProcessRef().setContainerId("container-1.0").setProcessId("process.1"))
                .setTarget(new ProcessRef().setContainerId("container-2.0").setProcessId("process.1"))
                .setDescription("plan description")
                .setMappings(Map.of("node1", "node2", "nodeA", "nodeB"));

        Mockito.when(planService.create(plan)).thenReturn(plan);

        Plan result = given()
                .when()
                .body(plan)
                .contentType(ContentType.JSON)
                .post("/plans")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().as(Plan.class);

        assertThat(result, is(plan));
    }
}
