package org.kie.processmigration.service;

import java.util.Base64;

import org.kie.processmigration.model.Credentials;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.credentials.EnteredCredentialsProvider;
import org.kie.server.client.credentials.EnteredTokenCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialsProviderFactory {

    private static final String CREDENTIALS_SEPARATOR = ":";

    private static final Logger logger = LoggerFactory.getLogger(CredentialsProviderFactory.class);

    public static Credentials getCredentials(String authHeader) {
        if (isBlank(authHeader)) {
            logger.debug("Request received without Auth header");
            return null;
        }
        if (authHeader.startsWith(CredentialsProvider.BASIC_AUTH_PREFIX)) {
            logger.debug("Extracting username/password for Scheme: Basic");
            String encoded = authHeader.substring(CredentialsProvider.BASIC_AUTH_PREFIX.length());
            String credentials = new String(Base64.getDecoder().decode(encoded));
            if (isBlank(credentials)) {
                return null;
            }
            String[] parts = credentials.split(CREDENTIALS_SEPARATOR);
            if (parts != null && parts.length == 2) {
                return new Credentials().setUsername(parts[0]).setPassword(parts[1]);
            }
        }
        if (authHeader.startsWith(CredentialsProvider.TOKEN_AUTH_PREFIX)) {
            logger.debug("Extracting token for Scheme: Bearer");
            String token = authHeader.substring(CredentialsProvider.TOKEN_AUTH_PREFIX.length());
            if (isBlank(token)) {
                return null;
            }
            return new Credentials().setToken(token);
        }
        logger.warn("Unable to extract credentials from: " + authHeader);
        return null;
    }

    public static CredentialsProvider getProvider(String authHeader) {
        return getProvider(getCredentials(authHeader));
    }

    public static CredentialsProvider getProvider(Credentials credentials) {
        if (credentials == null) {
            return null;
        }
        if (!isBlank(credentials.getToken())) {
            return new EnteredTokenCredentialsProvider(credentials.getToken());
        }
        return new EnteredCredentialsProvider(credentials.getUsername(), credentials.getPassword());
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
