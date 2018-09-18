package org.kie.processmigration.model;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

@ApplicationScoped
public class KieServerConfig {

    @Inject
    @ConfigurationValue("kieserver.host")
    private String host;
    @Inject
    @ConfigurationValue("kieserver.port")
    private Integer port;
    @Inject
    @ConfigurationValue("kieserver.contextRoot")
    private String contextRoot;
    @Inject
    @ConfigurationValue("kieserver.protocol")
    private String protocol;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUrl() {
        return new StringBuilder(protocol)
                                          .append("://")
                                          .append(host)
                                          .append(":")
                                          .append(port)
                                          .append(contextRoot)
                                          .append("/services/rest/server")
                                          .toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KieServerConfig [host=").append(host).append(", port=").append(port).append(", contextRoot=")
               .append(contextRoot).append(", protocol=").append(protocol).append("]");
        return builder.toString();
    }

}
