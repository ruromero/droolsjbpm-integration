package org.kie.processmigration.service;

import org.kie.server.api.model.KieServerStateInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;

public interface KieService {

    ProcessAdminServicesClient createProcessAdminServicesClient(CredentialsProvider credentialsProvider);

    KieServicesClient createKieServicesClient(CredentialsProvider credentialsProvider);

    ServiceResponse<KieServerStateInfo> getServerState(CredentialsProvider credentialsProvider);

}
