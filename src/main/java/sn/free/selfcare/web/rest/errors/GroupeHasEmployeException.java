package sn.free.selfcare.web.rest.errors;

public class GroupeHasEmployeException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupeHasEmployeException() {
        super(ErrorConstants.GROUP_HAS_EMPLOYEE, "Group has already employees!", "groupe", "grouphasemployee");
    }
}
