package org.kie.processmigration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Rule;
import org.junit.Test;
import org.kie.processmigration.model.Credentials;
import org.kie.processmigration.service.impl.CredentialsServiceImpl;
import org.kie.processmigration.service.util.ConfigurationValueProducer;

public class CredentialsServiceImplTest {

    @Rule
    public WeldInitiator weld = WeldInitiator.from(ConfigurationValueProducer.class, CredentialsServiceImpl.class).build();

    @Inject
    private CredentialsService service;

    @Test
    public void testSaveGet() {
        assertNotNull(service);
        Credentials cred = new Credentials().setMigrationId(1L).setUsername("kermit").setPassword("theFrog");
        Credentials saved = service.save(cred);
        assertNotNull(cred.getMigrationId());
        assertEquals(saved, cred);

        Credentials loaded = service.get(cred.getMigrationId());
        assertNotNull(loaded);
        assertEquals(cred.getMigrationId(), loaded.getMigrationId());
        assertEquals(cred.getUsername(), loaded.getUsername());
        assertEquals(cred.getPassword(), loaded.getPassword());
        assertNull(loaded.getToken());
    }

    @Test
    public void testSaveDelete() {
        assertNotNull(service);
        Credentials cred = new Credentials().setMigrationId(2L).setUsername("kermit").setPassword("theFrog");
        Credentials saved = service.save(cred);
        assertNotNull(cred.getMigrationId());
        assertEquals(saved, cred);

        Credentials loaded = service.delete(cred.getMigrationId());
        assertNotNull(loaded);
        assertEquals(cred.getMigrationId(), loaded.getMigrationId());
        assertEquals(cred.getUsername(), loaded.getUsername());
        assertEquals(cred.getPassword(), loaded.getPassword());
        assertNull(loaded.getToken());
        assertNull(service.get(cred.getMigrationId()));
    }

    @Test
    public void testSaveGetToken() {
        assertNotNull(service);
        Credentials cred = new Credentials().setMigrationId(3L).setToken("ZRi:K1BC&[-S:*c6;°§2]22PP]NR}Y/r^}8J,:O,B1({>Iz%3{h+P<.??w29U-P");
        Credentials saved = service.save(cred);
        assertNotNull(cred.getMigrationId());
        assertEquals(saved, cred);

        Credentials loaded = service.delete(cred.getMigrationId());
        assertNotNull(loaded);
        assertEquals(cred.getMigrationId(), loaded.getMigrationId());
        assertNull(loaded.getUsername());
        assertNull(loaded.getPassword());
        assertEquals(cred.getToken(), loaded.getToken());

        assertNull(service.get(cred.getMigrationId()));
    }
}
