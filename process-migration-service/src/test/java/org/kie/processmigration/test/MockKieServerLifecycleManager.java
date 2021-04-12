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

package org.kie.processmigration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.SneakyThrows;
import org.kie.server.api.model.*;
import org.kie.server.api.model.definition.NodeDefinition;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.ProcessDefinitionList;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.ProcessInstanceList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;


public class MockKieServerLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JaxbAnnotationModule());

    @SneakyThrows
    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();

        configureFor(wireMockServer.port());
        ServiceResponse<KieServerInfo> server1Response = getResponseFor("kie-server-1");
        stubFor(get(urlPathEqualTo(URI.create(server1Response.getResult().getLocation()).getPath()))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(server1Response))));

        ServiceResponse<KieServerInfo> server2Response = getResponseFor("kie-server-2");
        stubFor(get(urlPathEqualTo(URI.create(server2Response.getResult().getLocation()).getPath()))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(server2Response))));

        stubFor(get(urlPathEqualTo(wireMockServer.baseUrl() + "/not-found/services/rest/server"))
                .willReturn(notFound()));

        stubFor(get(urlPathEqualTo(wireMockServer.baseUrl() + "/unauthorized/services/rest/server"))
                .willReturn(unauthorized()));

        stubFor(get(urlPathEqualTo(wireMockServer.baseUrl() + "/forbidden/services/rest/server"))
                .withBasicAuth("other", "other123")
                .willReturn(forbidden()));

        stubContainers();

        Map<String, String> kieservers = new HashMap<>();
        kieservers.put("kieservers.server1.host", server1Response.getResult().getLocation());
        kieservers.put("kieservers.server1.username", "admin");
        kieservers.put("kieservers.server1.password", "admin123");
        kieservers.put("kieservers.server2.host", server2Response.getResult().getLocation());
        kieservers.put("kieservers.server2.username", "admin");
        kieservers.put("kieservers.server2.password", "admin123");
        kieservers.put("kieservers.server3.host", wireMockServer.baseUrl() + "/not-found/services/rest/server");
        kieservers.put("kieservers.server3.username", "unused");
        kieservers.put("kieservers.server3.password", "unused");
        kieservers.put("kieservers.server4.host", wireMockServer.baseUrl() + "/unauthorized/services/rest/server");
        kieservers.put("kieservers.server4.username", "unused");
        kieservers.put("kieservers.server4.password", "unused");
        kieservers.put("kieservers.server5.host", wireMockServer.baseUrl() + "/forbidden/services/rest/server");
        kieservers.put("kieservers.server5.username", "other");
        kieservers.put("kieservers.server5.password", "other123");
        kieservers.put("kieservers.server6.host", wireMockServer.baseUrl() + "/retry/services/rest/server");
        kieservers.put("kieservers.server6.username", "other");
        kieservers.put("kieservers.server6.password", "other123");
        return kieservers;
    }

    private void stubContainers() throws JsonProcessingException {
        ServiceResponse<KieContainerResourceList> emptyContainersResponse = new ServiceResponse<>(
                KieServiceResponse.ResponseType.SUCCESS,
                "List of created containers",
                new KieContainerResourceList());
        stubFor(get(urlPathEqualTo("/kie-server-1/services/rest/server/containers"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(emptyContainersResponse))));

        KieContainerResourceList containers = new KieContainerResourceList();
        containers.getContainers().add(new KieContainerResource("example-1.0.0", new ReleaseId("com.myspace", "Example", "1.0.0"), KieContainerStatus.STARTED));
        containers.getContainers().add(new KieContainerResource("example-1.0.1", new ReleaseId("com.myspace", "Example", "1.0.1"), KieContainerStatus.STARTED));
        ServiceResponse<KieContainerResourceList> containersResponse = new ServiceResponse<>(
                KieServiceResponse.ResponseType.SUCCESS,
                "List of created containers",
                containers);
        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/containers"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(containersResponse))));

        ProcessDefinition process1 = new ProcessDefinition();
        process1.setContainerId("example-1.0.0");
        process1.setId("process1");
        process1.setVersion("1.0");
        process1.setPackageName("org.kie.examples");
        process1.setName("The process 1");
        ProcessDefinitionList example100DefinitionsResponse = new ProcessDefinitionList(new ProcessDefinition[]{process1});
        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/queries/containers/example-1.0.0/processes/definitions"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(example100DefinitionsResponse))));

        ProcessDefinition process2 = new ProcessDefinition();
        process2.setContainerId("example-1.0.1");
        process2.setId("process1");
        process2.setVersion("1.1");
        process2.setPackageName("org.kie.examples");
        process2.setName("The process 1");

        ProcessDefinition process3 = new ProcessDefinition();
        process3.setContainerId("example-1.0.1");
        process3.setId("process2");
        process3.setVersion("1.0");
        process3.setPackageName("org.kie.examples");
        process3.setName("The process 2");
        NodeDefinition startNode = new NodeDefinition();
        startNode.setId(1L);
        startNode.setType("StartNode");
        startNode.setUniqueId(UUID.randomUUID().toString());
        startNode.setName("Start Here");
        NodeDefinition endNode = new NodeDefinition();
        endNode.setId(2L);
        endNode.setType("EndNode");
        endNode.setUniqueId(UUID.randomUUID().toString());
        endNode.setName("End Here");
        Collection<NodeDefinition> nodes = List.of(endNode, endNode);
        process3.setNodes(nodes);

        ProcessDefinitionList example101DefinitionsResponse = new ProcessDefinitionList(new ProcessDefinition[]{process2, process3});
        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/queries/containers/example-1.0.1/processes/definitions"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(example101DefinitionsResponse))));
        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/queries/containers/example-1.0.1/processes/definitions/process2"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(process2))));

        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/containers/example-1.0.1/processes/definitions/process2"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(process3))));
        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/containers/example-1.0.1/images/processes/process2"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okXml("<test/>")));


        ProcessInstance instance1 = new ProcessInstance();
        instance1.setId(9L);
        instance1.setContainerId("example-1.0.1");
        instance1.setProcessId("process2");
        instance1.setState(1);
        instance1.setDate(new Date());
        ProcessInstanceList instances = new ProcessInstanceList(new ProcessInstance[]{instance1});
        stubFor(get(urlPathEqualTo("/kie-server-2/services/rest/server/containers/example-1.0.1/processes/instances"))
                .withBasicAuth("admin", "admin123")
                .willReturn(okJson(mapper.writeValueAsString(instances))));

    }

    public void enableRetryServer() throws JsonProcessingException {
        configureFor(wireMockServer.port());
        ServiceResponse<KieServerInfo> retryResponse = getResponseFor("retry");
        stubFor(get(urlPathEqualTo(URI.create(retryResponse.getResult().getLocation()).getPath()))
                .withBasicAuth("other", "other123")
                .willReturn(okJson(mapper.writeValueAsString(retryResponse))));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Override
    public void inject(Object testInstance) {
        Class<?> c = testInstance.getClass();
        Class<? extends Annotation> annotation = TestKieServer.class;
        Class<?> injectedClass = MockKieServerLifecycleManager.class;
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getAnnotation(annotation) != null) {
                    if (!injectedClass.isAssignableFrom(f.getType())) {
                        throw new RuntimeException(annotation + " can only be used on fields of type " + injectedClass);
                    }

                    f.setAccessible(true);
                    try {
                        f.set(testInstance, this);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            c = c.getSuperclass();
        }
    }

    public ServiceResponse<KieServerInfo> getResponseFor(String serverName) {
        KieServerInfo info = new KieServerInfo(serverName,
                serverName,
                "99.0-SNAPSHOT",
                List.of("KieServer", "BRM", "BPM", "CaseMgmt", "BPM-UI", "BRP", "DMN", "Swagger", "Prometheus"),
                wireMockServer.baseUrl() + "/" + serverName + "/services/rest/server");
        return new ServiceResponse<>(KieServiceResponse.ResponseType.SUCCESS, "Kie Server info", info);
    }

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public void reset() {
        wireMockServer.resetRequests();
    }
}
