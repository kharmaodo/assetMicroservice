package sn.free.selfcare.web.rest.errors;

public class LigneAlreadyAttribuedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LigneAlreadyAttribuedException() {
        super(ErrorConstants.LINE_ALREADY_ATTRIBUTED, "Line already attributed to an employee!", "ligne", "lineattributed");
    }
}
