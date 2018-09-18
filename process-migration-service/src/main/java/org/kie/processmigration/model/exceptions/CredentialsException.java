package org.kie.processmigration.model.exceptions;

public class CredentialsException extends Exception {

    private static final long serialVersionUID = -6659354310702225168L;

    public CredentialsException(String message) {
        super(message);
    }

    public CredentialsException(String message, Exception e) {
        super(message, e);
    }

    public CredentialsException(Exception e) {
        super(e);
    }
}
