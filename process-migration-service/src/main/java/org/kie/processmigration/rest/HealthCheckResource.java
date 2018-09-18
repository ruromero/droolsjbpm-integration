/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.processmigration.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.health.Health;
import org.kie.processmigration.model.HealthStatus;
import org.kie.processmigration.service.CredentialsProviderFactory;
import org.kie.processmigration.service.KieService;
import org.kie.server.api.model.KieServerStateInfo;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.CredentialsProvider;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheckResource {

    @Inject
    private KieService kieService;

    @GET
    @Path("/readiness")
    @Health
    public Response checkReadiness() {
        return Response.ok(new HealthStatus().up()).build();
    }

    @GET
    @Path("/liveness")
    @Health
    public Response checkLiveness(@Context HttpHeaders headers) {
        CredentialsProvider credProvider = CredentialsProviderFactory.getProvider(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
        ServiceResponse<KieServerStateInfo> serverState = kieService.getServerState(credProvider);
        HealthStatus status = new HealthStatus();
        if (serverState == null || !ResponseType.SUCCESS.equals(serverState.getType())) {
            status = status.down();
        } else {
            status = status.up();
        }
        if (serverState != null) {
            status = status.setMessage(serverState.getMsg());
        }
        if (status.isUp()) {
            return Response.ok(status).build();
        } else {
            return Response.serverError().entity(status).build();
        }

    }

}
