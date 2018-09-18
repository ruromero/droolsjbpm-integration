package org.kie.processmigration.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.kie.processmigration.jpa.converter.CryptoConverter;

/**
 * Stores the credentials to use for communicating against KIE Server.
 * These credentials must be safely stored in the database.
 * The migrationId is indirectly referenced to avoid dependencies
 */
@Entity
@Table(name = "credentials")
@NamedQueries({
               @NamedQuery(name = "Credentials.findByMigrationId", query = "SELECT p FROM Credentials p WHERE p.migrationId = :id")
})
public class Credentials {

    @Id
    @Column(name = "migration_id")
    private Long migrationId;

    private String token;

    @Convert(converter = CryptoConverter.class)
    private String username;

    @Convert(converter = CryptoConverter.class)
    private String password;

    public Long getMigrationId() {
        return migrationId;
    }

    public Credentials setMigrationId(Long migrationId) {
        this.migrationId = migrationId;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Credentials setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Credentials setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Credentials setPassword(String password) {
        this.password = password;
        return this;
    }
}
