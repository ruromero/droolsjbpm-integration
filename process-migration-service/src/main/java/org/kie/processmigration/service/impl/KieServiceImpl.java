package org.kie.processmigration.service.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.processmigration.model.KieServerConfig;
import org.kie.processmigration.service.KieService;
import org.kie.server.api.model.KieServerStateInfo;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.admin.ProcessAdminServicesClient;
import org.kie.server.client.admin.impl.ProcessAdminServicesClientImpl;
import org.kie.server.client.impl.KieServicesClientImpl;
import org.kie.server.client.impl.KieServicesConfigurationImpl;

@ApplicationScoped
public class KieServiceImpl implements KieService {

    @Inject
    private KieServerConfig config;

    public ServiceResponse<KieServerStateInfo> getServerState(CredentialsProvider credentialsProvider) {
        try {
            return createKieServicesClient(credentialsProvider).getServerState();
        } catch (Exception e) {
            ServiceResponse<KieServerStateInfo> response = new ServiceResponse<>();
            response.setType(ResponseType.FAILURE);
            response.setMsg(e.getMessage());
            return response;
        }
    }

    public ProcessAdminServicesClient createProcessAdminServicesClient(CredentialsProvider credentialsProvider) {
        KieServicesConfiguration kieServicesConfig = new KieServicesConfigurationImpl(config.getUrl(), credentialsProvider);
        ProcessAdminServicesClientImpl processAdminServicesClient = new ProcessAdminServicesClientImpl(kieServicesConfig);
        processAdminServicesClient.setOwner((KieServicesClientImpl) createKieServicesClient(credentialsProvider));
        return processAdminServicesClient;
    }

    public KieServicesClient createKieServicesClient(CredentialsProvider credentialsProvider) {
        KieServicesConfiguration kieServicesConfig = new KieServicesConfigurationImpl(config.getUrl(), credentialsProvider);
        return new KieServicesClientImpl(kieServicesConfig);
    }

}
