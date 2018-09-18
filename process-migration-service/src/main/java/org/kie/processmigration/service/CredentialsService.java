package org.kie.processmigration.service;

import org.kie.processmigration.model.Credentials;

public interface CredentialsService {

    Credentials get(Long id);

    Credentials save(Credentials credentials);

    Credentials delete(Long id);

}
