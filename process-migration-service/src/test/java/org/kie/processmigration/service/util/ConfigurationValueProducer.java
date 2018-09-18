package org.kie.processmigration.service.util;

import java.io.FileNotFoundException;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;
import org.yaml.snakeyaml.Yaml;

public class ConfigurationValueProducer {

    Map<String, String> configuration;

    @SuppressWarnings("unchecked")
    public ConfigurationValueProducer() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        configuration = yaml.loadAs(this.getClass().getClassLoader().getResourceAsStream("project-defaults.yml"), Map.class);
    }

    @Produces
    @ConfigurationValue("")
    public String getStringconfigValue(InjectionPoint ip) {
        ConfigurationValue annotation = ip.getAnnotated().getAnnotation(ConfigurationValue.class);
        return configuration.get(annotation.value());
    }
}
