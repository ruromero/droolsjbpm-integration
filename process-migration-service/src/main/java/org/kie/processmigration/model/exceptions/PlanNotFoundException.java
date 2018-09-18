package org.kie.processmigration.model.exceptions;

public class PlanNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6515261252903711692L;
    private final Long id;

    public PlanNotFoundException(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return "Plan not found with id: " + id;
    }

}
